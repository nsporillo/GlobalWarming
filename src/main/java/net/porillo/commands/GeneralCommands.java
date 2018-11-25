package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.porillo.GlobalWarming;

import net.porillo.config.Lang;
import net.porillo.database.queries.insert.OffsetInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.OffsetTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.engine.models.CarbonIndexModel;
import net.porillo.objects.GPlayer;
import net.porillo.objects.OffsetBounty;
import net.porillo.util.ChatTable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.ChatColor.*;

@CommandAlias("globalwarming|gw")
public class GeneralCommands extends BaseCommand {
    private static final long SPAM_INTERVAL_TICKS = GlobalWarming.getInstance().getConf().getSpamInterval();
    private static final UUID untrackedUUID = UUID.fromString("1-1-1-1-1");
    public static final double LOW_TEMPERATURE_UBOUND = GlobalWarming.getInstance().getConf().getLowTemperatureUBound();
    public static final double HIGH_TEMPERATURE_LBOUND = GlobalWarming.getInstance().getConf().getHighTemperatureLBound();
    private static final int MAX_BOUNTIES_CREATED_PER_PLAYER = GlobalWarming.getInstance().getConf().getMaxBounties();
    private List<UUID> playerRequestList;

    public GeneralCommands() {
        playerRequestList = new ArrayList<>();
        debounceRequests();
    }

    @HelpCommand
    public void onHelp(GPlayer gPlayer, CommandHelp help) {
        if (isCommandAllowed(gPlayer)) {
            help.showHelp();
        }
    }

    @Subcommand("bounty")
    @CommandPermission("globalwarming.bounty")
    public class BountyCommand extends BaseCommand {

        @Subcommand("")
        @Description("Display all active bounties")
        @Syntax("")
        @CommandPermission("globalwarming.bounty")
        public void onBounty(GPlayer gPlayer) {
            if (isCommandAllowed(gPlayer)) {
                showBounties(gPlayer);
            }
        }

        @Subcommand("create")
        @Description("Create a tree-planting bounty to reduce your carbon footprint")
        @Syntax("[tree-blocks] [reward]")
        @CommandPermission("globalwarming.bounty.create")
        public void onBountyCreate(GPlayer gPlayer, String[] args) {
            if (isCommandAllowed(gPlayer)) {
                int treeBlocks = 0;
                int reward = 0;
                if (args.length == 2) {
                    treeBlocks = Integer.parseInt(args[0]);
                    reward = Integer.parseInt(args[1]);
                }

                if (treeBlocks > 0 && reward > 0) {
                    createBounty(gPlayer, treeBlocks, reward);
                } else {
                    gPlayer.sendMsg(String.format(
                          Lang.GENERIC_INVALIDARGS.get(),
                          "[tree-blocks:integer] [reward:integer]"));
                }
            }
        }

        @Subcommand("join")
        @Description("Join a tree-planting bounty for a reward (see: /gw bounty list)")
        @Syntax("[bounty_id]")
        @CommandPermission("globalwarming.bounty.join")
        public void onBountyJoin(GPlayer gPlayer, String[] args) {
            if (isCommandAllowed(gPlayer)) {
                int bountyId = 0;
                if (args.length == 1) {
                    bountyId = Integer.parseInt(args[0]);
                }

                if (bountyId > 0) {
                    joinBounty(gPlayer, bountyId);
                } else {
                    gPlayer.sendMsg(String.format(
                          Lang.GENERIC_INVALIDARGS.get(),
                          "[bounty_id:integer]"));
                }
            }
        }

        @Subcommand("unjoin")
        @Description("Abandon a bounty you joined")
        @Syntax("")
        @CommandPermission("globalwarming.bounty.cancel")
        public void onBountyUnjoin(GPlayer gPlayer) {
            if (isCommandAllowed(gPlayer)) {
                OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
                OffsetBounty bounty = offsetTable.unJoin(gPlayer);
                if (bounty == null) {
                    gPlayer.sendMsg(Lang.BOUNTY_NOTJOINED);
                } else {
                    notifyBounty(
                          bounty,
                          gPlayer,
                          String.format(Lang.BOUNTY_ABANDONEDBY.get(), gPlayer.getPlayer().getName()),
                          Lang.BOUNTY_ABANDONED.get()
                    );
                }
            }
        }

        @Subcommand("cancel")
        @Description("Cancel any idle bounties you created")
        @Syntax("")
        @CommandPermission("globalwarming.bounty.cancel")
        public void onBountyCancel(GPlayer gPlayer) {
            if (isCommandAllowed(gPlayer)) {
                OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
                int count = offsetTable.cancel(gPlayer);
                gPlayer.sendMsg(String.format(Lang.BOUNTY_CANCELLED.get(), count));
            }
        }
    }

    @Subcommand("score")
    @CommandPermission("globalwarming.score")
    public class ScoreCommand extends BaseCommand {

        @Subcommand("")
        @Description("Get your carbon score")
        @Syntax("")
        @CommandPermission("globalwarming.score")
        public void onScore(GPlayer gPlayer) {
            if (isCommandAllowed(gPlayer)) {
                showCarbonScore(gPlayer);
            }
        }

        @Subcommand("show")
        @Description("Show the scoreboard")
        @Syntax("")
        @CommandPermission("globalwarming.score.show")
        public void onShow(GPlayer gPlayer) {
            if (isCommandAllowed(gPlayer)) {
                Player player = gPlayer.getPlayer();
                GlobalWarming.getInstance().getScoreboard().show(player, true);
            }
        }

        @Subcommand("hide")
        @Description("Hide the scoreboard")
        @Syntax("")
        @CommandPermission("globalwarming.score.hide")
        public void onHide(GPlayer gPlayer) {
            if (isCommandAllowed(gPlayer)) {
                Player player = gPlayer.getPlayer();
                GlobalWarming.getInstance().getScoreboard().show(player, false);
            }
        }
    }

    @Subcommand("top")
    @CommandPermission("globalwarming.top")
    public class TopCommand extends BaseCommand {

        @Subcommand("")
        @Description("Display the top ten polluters and planters")
        @CommandPermission("globalwarming.top")
        public void onTop(GPlayer gPlayer) {
            if (isCommandAllowed(gPlayer)) {
                showTopTen(gPlayer, true);
                showTopTen(gPlayer, false);
            }
        }

        @Subcommand("polluter")
        @Description("Display the top ten polluters")
        @CommandPermission("globalwarming.top.polluter")
        public void onTopPolluter(GPlayer gPlayer) {
            if (isCommandAllowed(gPlayer)) {
                showTopTen(gPlayer, true);
            }
        }

        @Subcommand("planter")
        @Description("Display the top ten tree-planters")
        @CommandPermission("globalwarming.top.planter")
        public void onTopPlanter(GPlayer gPlayer) {
            if (isCommandAllowed(gPlayer)) {
                showTopTen(gPlayer, false);
            }
        }
    }

    /**
     * True when:
     * - The player is not spamming
     * - The player's climate-engine is enabled
     */
    private boolean isCommandAllowed(GPlayer gPlayer) {
        boolean isCommandAllowed = false;
        if (isSpamming(gPlayer)) {
            gPlayer.sendMsg(Lang.GENERIC_SPAM);
        } else if (!ClimateEngine.getInstance().isAssociatedEngineEnabled(gPlayer)) {
            gPlayer.sendMsg(Lang.ENGINE_DISABLED);
        } else {
            isCommandAllowed = true;
        }

        return isCommandAllowed;
    }

    /**
     * Limit player requests per interval
     * - Valid requests store that player in a list
     * - The player-request list is cleared periodically
     */
    private boolean isSpamming(GPlayer gPlayer) {
        boolean isSpamming = true;
        if (gPlayer != null) {
            synchronized (this) {
                if (!playerRequestList.contains(gPlayer.getUuid())) {
                    playerRequestList.add(gPlayer.getUuid());
                    isSpamming = false;
                }
            }
        }

        return isSpamming;
    }

    /**
     * Clear the spam list periodically
     */
    private void debounceRequests() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
              GlobalWarming.getInstance(),
              () -> {
                  synchronized (this) {
                      playerRequestList.clear();
                  }
              }, 0L, SPAM_INTERVAL_TICKS);
    }

    /**
     * Format a carbon index
     * - Map the value to color heat
     * - Maximum of two decimal places
     */
    private static String formatIndex(double index, int score) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s",
              getScoreColor(score),
              decimalFormat.format(index));
    }

    /**
     * Format a carbon score
     * - Map the value to color heat
     */
    private static String formatScore(int score) {
        return String.format("%s%d",
              getScoreColor(score),
              score);
    }

    /**
     * Format a temperature
     * - Map the value to color heat
     * - Maximum of two decimal places
     */
    private static String formatTemperature(double temperature) {
        ChatColor color = getTemperatureColor(temperature);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s",
              color,
              decimalFormat.format(temperature));
    }

    /**
     * Get the color associated with a carbon score
     * - Values are mapped to color-heat from LOW CO2 (cold) to HIGH CO2 (hot)
     * - These ranges are somewhat arbitrary
     */
    public static ChatColor getScoreColor(int score) {
        ChatColor color;
        if (score <= -3500) {
            color = DARK_BLUE;
        } else if (score <= -2500) {
            color = BLUE;
        } else if (score <= -1500) {
            color = DARK_AQUA;
        } else if (score <= -500) {
            color = AQUA;
        } else if (score <= 500) {
            color = GREEN; // (-500, 500]
        } else if (score <= 1500) {
            color = YELLOW;
        } else if (score <= 2500) {
            color = GOLD;
        } else if (score <= 3500) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        return color;
    }

    /**
     * Get the color associated with a temperature
     * - These ranges are somewhat arbitrary
     */
    public static ChatColor getTemperatureColor(double temperature) {
        ChatColor color;
        if (temperature <= 10.5) {
            color = DARK_BLUE;
        } else if (temperature <= 11.5) {
            color = BLUE;
        } else if (temperature <= 12.5) {
            color = DARK_AQUA;
        } else if (temperature <= 13.5) {
            color = AQUA;
        } else if (temperature <= 14.5) {
            color = GREEN; // (13.5, 14.5]
        } else if (temperature <= 15.5) {
            color = YELLOW;
        } else if (temperature <= 16.5) {
            color = GOLD;
        } else if (temperature <= 17.5) {
            color = LIGHT_PURPLE;
        } else if (temperature <= 18.5) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        return color;
    }

    /**
     * TODO: Add economy integration
     */
    private static void createBounty(GPlayer gPlayer, int treeBlocks, int reward) {
        OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
        if (offsetTable.getIncompleteBountyCount(gPlayer) >= MAX_BOUNTIES_CREATED_PER_PLAYER) {
            gPlayer.sendMsg(Lang.BOUNTY_MAXCREATED);
        } else {
            //New bounty:
            OffsetBounty bounty = new OffsetBounty(
                  GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE),
                  gPlayer.getUniqueId(),
                  null,
                  ClimateEngine.getInstance().getAssociatedWorldName(gPlayer.getPlayer()),
                  treeBlocks,
                  reward,
                  0,
                  0
            );

            //Local records:
            offsetTable.addOffset(bounty);

            //Database:
            OffsetInsertQuery insertQuery = new OffsetInsertQuery(bounty);
            AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

            //Notify:
            //TODO: hold the reward
            gPlayer.sendMsg(Lang.BOUNTY_CREATED);
        }
    }

    /**
     * Show all active bounties in the player's associated world
     */
    private static void showBounties(GPlayer gPlayer) {
        String associatedWorldName = ClimateEngine.getInstance().getAssociatedWorldName(gPlayer.getPlayer());
        ChatTable chatTable = new ChatTable(Lang.BOUNTY_TITLE.get());
        chatTable.setGridColor(ChatColor.BLUE);
        chatTable.addHeader(Lang.BOUNTY_PLAYER.get(), 75);
        chatTable.addHeader(Lang.BOUNTY_HUNTER.get(), 75);
        chatTable.addHeader(Lang.BOUNTY_BLOCKS.get(), 65);
        chatTable.addHeader(Lang.BOUNTY_REWARD.get(), 65);

        try {
            List<Integer> clickIds = new ArrayList<>();
            OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
            PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
            for (OffsetBounty offsetBounty : offsetTable.getOffsetList()) {
                //Ignore completed bounties and bounties not from the player's world:
                if (offsetBounty.getTimeCompleted() != 0 || !offsetBounty.getWorldName().equals(associatedWorldName)) {
                    continue;
                }

                //Creator:
                List<String> row = new ArrayList<>();
                UUID creator = playerTable.getUuidMap().get(offsetBounty.getCreatorId());
                String creatorName = Bukkit.getOfflinePlayer(creator).getName();
                row.add(creatorName);

                //Show the hunter-name:
                if (offsetBounty.getHunterId() != null) {
                    UUID hunter = playerTable.getUuidMap().get(offsetBounty.getHunterId());
                    if (hunter != null) {
                        String hunterName = Bukkit.getOfflinePlayer(hunter).getName();
                        if (hunterName != null) {
                            row.add(hunterName);
                        }
                    }
                }

                //Or a link to join the bounty:
                // - Track bounty IDs for click-events:
                if (row.size() == 1) {
                    row.add(Lang.BOUNTY_JOIN.get());
                    clickIds.add(offsetBounty.getUniqueId());
                }

                //Remaining tree-blocks:
                row.add(offsetBounty.getLogBlocksTarget().toString());

                //Reward:
                row.add(String.format("$%s", offsetBounty.getReward()));

                //Add table row:
                chatTable.addRow(row);
            }

            String json = chatTable.toJson(gPlayer, Lang.BOUNTY_JOIN.get(), "/gw bounty join", clickIds);
            Bukkit.getServer().dispatchCommand(
                  Bukkit.getConsoleSender(),
                  json);
        } catch (Exception e) {
            gPlayer.sendMsg(Lang.BOUNTY_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Join a bounty
     * - Players are limited to joining one bounty
     * - Bounties are limited to one hunter
     */
    private static void joinBounty(GPlayer gPlayer, int bountyId) {
        try {
            //Local and database update:
            OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
            OffsetBounty bounty = offsetTable.join(gPlayer, bountyId);
            if (bounty != null) {
                notifyBounty(
                      bounty,
                      String.format(Lang.BOUNTY_ACCEPTEDBY.get(), gPlayer.getPlayer().getName()),
                      Lang.BOUNTY_ACCEPTED.get()
                );
            }
        } catch (Exception e) {
            if (e.getMessage().length() > 0) {
                gPlayer.sendMsg(e.getMessage());
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send messages to the creator and hunter of a bounty
     */
    private static void notifyBounty(OffsetBounty bounty, String creatorMessage, String hunterMessage) {
        PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
        UUID uuid = playerTable.getUuidMap().get(bounty.getHunterId());
        GPlayer hunter = playerTable.getPlayers().get(uuid);
        notifyBounty(bounty, hunter, creatorMessage, hunterMessage);
    }

    /**
     * Send messages to the creator and hunter of a bounty
     */
    public static void notifyBounty(OffsetBounty bounty, GPlayer hunter, String creatorMessage, String hunterMessage) {
        //Notify bounty hunter:
        if (hunter != null) {
            hunter.sendMsg(hunterMessage);
        }

        //Notify bounty creator:
        PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
        UUID uuid = playerTable.getUuidMap().get(bounty.getCreatorId());
        GPlayer creator = playerTable.getPlayers().get(uuid);
        if (creator != null && hunter != null) {
            creator.sendMsg(creatorMessage);
        }
    }

    /**
     * Show the player's carbon score as a chat message
     */
    private static void showCarbonScore(GPlayer gPlayer) {
        Player player = gPlayer.getPlayer();
        if (player != null) {
            //Do not show scored for worlds with disabled climate-engines:
            // - Note: temperature is based on the player's associated-world (not the current world)
            WorldClimateEngine associatedClimateEngine =
                  ClimateEngine.getInstance().getAssociatedClimateEngine(player);

            if (associatedClimateEngine != null && associatedClimateEngine.isEnabled()) {
                int score = gPlayer.getCarbonScore();
                double temperature = associatedClimateEngine.getTemperature();
                gPlayer.sendMsg(
                      Lang.SCORE_CHAT,
                      formatScore(score),
                      formatTemperature(temperature));

                //Guidance based on the global temperature:
                if (temperature < LOW_TEMPERATURE_UBOUND) {
                    gPlayer.sendMsg(Lang.TEMPERATURE_LOW);
                } else if (temperature < HIGH_TEMPERATURE_LBOUND) {
                    gPlayer.sendMsg(Lang.TEMPERATURE_BALANCED);
                } else {
                    gPlayer.sendMsg(String.format("%s%s",
                          Lang.TEMPERATURE_HIGH.get(),
                          GlobalWarming.getEconomy() == null
                          ? ""
                          : Lang.TEMPERATURE_HIGHWITHBOUNTY.get()));
                }
            } else {
                gPlayer.sendMsg(Lang.ENGINE_DISABLED);
            }
        }
    }

    /**
     * Show the top 10 polluters or planters as a chat message
     */
    private static void showTopTen(GPlayer gPlayer, boolean isPolluterList) {
        if (ClimateEngine.getInstance().isAssociatedEngineEnabled(gPlayer)) {
            CarbonIndexModel indexModel = ClimateEngine.getInstance().getAssociatedClimateEngine(gPlayer.getPlayer()).getCarbonIndexModel();
            ChatTable chatTable = new ChatTable(isPolluterList ? Lang.TOPTABLE_POLLUTERS.get() : Lang.TOPTABLE_PLANTERS.get());
            chatTable.setGridColor(isPolluterList ? ChatColor.DARK_RED : ChatColor.GREEN);
            chatTable.addHeader(Lang.TOPTABLE_PLAYER.get(), 130);
            chatTable.addHeader(Lang.TOPTABLE_INDEX.get(), 75);
            chatTable.addHeader(Lang.TOPTABLE_SCORE.get(), 75);

            try {
                PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
                List<GPlayer> players = new ArrayList<>(playerTable.getPlayers().values());
                players.sort(Comparator.comparing(GPlayer::getCarbonScore));
                if (isPolluterList) {
                    Collections.reverse(players);
                }

                int rowCount = 0;
                for (GPlayer player : players) {
                    List<String> row = new ArrayList<>();
                    if (player.getUuid().equals(untrackedUUID)) {
                        continue;
                    }

                    int score = player.getCarbonScore();
                    double index = indexModel.getCarbonIndex(score);
                    row.add(Bukkit.getOfflinePlayer(player.getUuid()).getName());
                    row.add(formatIndex(index, score));
                    row.add(formatScore(score));
                    chatTable.addRow(row);
                    if (++rowCount == 10) {
                        break;
                    }
                }

                gPlayer.sendMsg(chatTable.toString());
            } catch (Exception e) {
                gPlayer.sendMsg(Lang.TOPTABLE_ERROR);
                e.printStackTrace();
            }
        } else {
            gPlayer.sendMsg(Lang.ENGINE_DISABLED);
        }
    }
}