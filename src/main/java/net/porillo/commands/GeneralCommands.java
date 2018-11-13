package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.porillo.GlobalWarming;

import net.porillo.config.Lang;
import net.porillo.database.tables.OffsetTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.engine.models.CarbonIndexModel;
import net.porillo.objects.GPlayer;
import net.porillo.objects.OffsetBounty;
import net.porillo.util.ChatTable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.ChatColor.*;

@CommandAlias("globalwarming|gw")
public class GeneralCommands extends BaseCommand {
    private static final long SPAM_INTERVAL_TICKS = 60;
    private static final UUID untrackedUUID = UUID.fromString("1-1-1-1-1");
    public static final double LOW_TEMPERATURE_UBOUND = 13.75;
    public static final double HIGH_TEMPERATURE_LBOUND = 14.25;
    private List<UUID> playerRequestList;

    public GeneralCommands() {
        playerRequestList = new ArrayList<>();
        debounceRequests();
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
                } else {
                    gPlayer.sendMsg(Lang.GENERIC_SPAM.get());
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

    @Subcommand("score")
    @CommandPermission("globalwarming.score")
    public class ScoreCommand extends BaseCommand {
        @Subcommand("")
        @Description("Get your carbon score")
        @Syntax("")
        @CommandPermission("globalwarming.score")
        public void onScore(GPlayer gPlayer) {
            if (!isSpamming(gPlayer)) {
                showCarbonScore(gPlayer);
            }
        }

        @Subcommand("show")
        @Description("Show the scoreboard")
        @Syntax("")
        @CommandPermission("globalwarming.score.show")
        public void onShow(GPlayer gPlayer) {
            if (!isSpamming(gPlayer)) {
                Player player = gPlayer.getPlayer();
                GlobalWarming.getInstance().getScoreboard().show(player, true);
            }
        }

        @Subcommand("hide")
        @Description("Hide the scoreboard")
        @Syntax("")
        @CommandPermission("globalwarming.score.hide")
        public void onHide(GPlayer gPlayer) {
            if (!isSpamming(gPlayer)) {
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
            if (!isSpamming(gPlayer)) {
                showTopTen(gPlayer, true);
                showTopTen(gPlayer, false);
            }
        }

        @Subcommand("polluter")
        @Description("Display the top ten polluters")
        @CommandPermission("globalwarming.top.polluter")
        public void onTopPolluter(GPlayer gPlayer) {
            if (!isSpamming(gPlayer)) {
                showTopTen(gPlayer, true);
            }
        }

        @Subcommand("planter")
        @Description("Display the top ten tree-planters")
        @CommandPermission("globalwarming.top.planter")
        public void onTopPlanter(GPlayer gPlayer) {
            if (!isSpamming(gPlayer)) {
                showTopTen(gPlayer, false);
            }
        }
    }

    @Subcommand("bounty")
    @CommandPermission("globalwarming.bounty")
    public class BountyCommand extends BaseCommand {

        @Subcommand("offset")
        @Description("Set tree-planting bounties to reduce carbon footprint")
        @Syntax("[log] [reward]")
        @CommandPermission("globalwarming.bounty.offset")
        public void onBountyOffset(GPlayer gPlayer, String[] args) {
            if (!isSpamming(gPlayer)) {
                // Validate input
                Integer logTarget;
                Integer reward;

                if (args.length != 2) {
                    gPlayer.sendMsg(RED + "Must specify 2 args");
                }
                try {
                    logTarget = Integer.parseInt(args[0]);
                    reward = Integer.parseInt(args[1]);

                    if (logTarget <= 0 || reward <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException nfe) {
                    gPlayer.sendMsg(RED + "Error: <trees> and <reward> must be positive integers");
                    return;
                }

                //TODO: Add economy integration
                OffsetBounty bounty = new OffsetBounty();
                bounty.setCreator(gPlayer);
                bounty.setLogBlocksTarget(logTarget);
                bounty.setReward(reward);
            }
        }

        // TODO: When listing bounties, add a clickable chat link to easily start job
        // TODO: Add configurable player max concurrent bounties to prevent bounty hoarding
        @Subcommand("list")
        @Description("Show all current bounties")
        @Syntax("")
        @CommandPermission("globalwarming.bounty.list")
        public void onBounty(GPlayer gPlayer) {
            if (!isSpamming(gPlayer)) {
                OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
                Player player = gPlayer.getPlayer();

                int numBounties = offsetTable.getOffsetList().size();
                gPlayer.sendMsg(GREEN + "Showing " + numBounties + " Tree Planting Bounties");

                // TODO: Paginate if necessary
                for (OffsetBounty bounty : offsetTable.getOffsetList()) {
                    if (bounty.isAvailable()) {
                        //bounty.showPlayerDetails(player);
                    }
                }
            }
        }
    }

    @HelpCommand
    public void onHelp(GPlayer gPlayer, CommandHelp help) {
        if (!isSpamming(gPlayer)) {
            help.showHelp();
        }
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
                    gPlayer.sendMsg(Lang.TEMPERATURE_HIGH);
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
            chatTable.setDelimiter(ChatTable.Section.HEADER, '+');
            chatTable.setDelimiter(ChatTable.Section.BODY, '|');
            chatTable.setDefaultColor(isPolluterList ? ChatColor.DARK_RED : ChatColor.GREEN);
            chatTable.setTextColor(ChatTable.Section.HEADER, ChatColor.WHITE);
            chatTable.addHeader(Lang.TOPTABLE_PLAYER.get(), 125);
            chatTable.addHeader(Lang.TOPTABLE_INDEX.get(), 50);
            chatTable.addHeader(Lang.TOPTABLE_SCORE.get(), 50);

            try {
                Connection connection = GlobalWarming.getInstance().getConnectionManager().openConnection();
                PreparedStatement statement = connection.prepareStatement(String.format(
                      "SELECT uuid, carbonScore FROM players ORDER BY carbonScore %s LIMIT 10",
                      isPolluterList
                            ? "DESC"
                            : "ASC"));

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    List<String> row = new ArrayList<>();
                    UUID uuid = UUID.fromString(result.getString(1));
                    if (uuid.equals(untrackedUUID)) {
                        continue;
                    }

                    int score = result.getInt(2);
                    double index = indexModel.getCarbonIndex(score);
                    row.add(Bukkit.getOfflinePlayer(uuid).getName());
                    row.add(formatIndex(index, score));
                    row.add(formatScore(score));
                    chatTable.addRow(row);
                }

                gPlayer.sendMsg(chatTable.toString());
            } catch (Exception e) {
                gPlayer.sendMsg(Lang.TOPTABLE_ERROR.get());
                e.printStackTrace();
            }
        } else {
            gPlayer.sendMsg(Lang.ENGINE_DISABLED);
        }
    }
}