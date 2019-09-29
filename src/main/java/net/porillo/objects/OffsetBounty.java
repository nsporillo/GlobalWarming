package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.database.queries.insert.OffsetInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.OffsetTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.util.ChatTable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffsetBounty {

    private static final int MAX_BOUNTIES_CREATED_PER_PLAYER = GlobalWarming.getInstance().getConf().getMaxBounties();

    /**
     * Unique integer ID of this offset bounty
     */
    private Integer uniqueId;

    /**
     * The player who created this carbon offset bounty
     */
    private Integer creatorId;

    /**
     * The player who is fulfilling this carbon offset bounty
     * Null if the bounty is available to be picked up.
     * TODO: Consider allowing multiple players to participate
     * TODO: in someone's bounty, and the reward be split evenly
     */
    private Integer hunterId;

    /**
     * World the offset bounty belongs to
     */
    private UUID worldId;

    /**
     * The required number of log blocks that need to be
     * grown by the hunter before this bounty is completed
     */
    private Integer logBlocksTarget;

    /**
     * The player defined reward for bounty completion
     */
    private Integer reward;

    /**
     * Variables to track time
     */
    private long timeStarted, timeCompleted;

    public OffsetBounty(ResultSet rs) throws SQLException {
        this.uniqueId = rs.getInt(1);
        this.creatorId = rs.getInt(2);
        this.hunterId = rs.getInt(3);
        this.worldId = UUID.fromString(rs.getString(4));
        this.logBlocksTarget = rs.getInt(5);
        this.reward = rs.getInt(6);
        this.timeStarted = rs.getLong(7);
        this.timeCompleted = rs.getLong(8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OffsetBounty bounty = (OffsetBounty) o;

        return uniqueId.equals(bounty.uniqueId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + uniqueId.hashCode();
        return result;
    }

    /**
     * Create a new bounty
     * - Players are limited to how many bounties they can create
     * - Requires an economy-plugin account with an adequate balance
     * - Requires a minimum of 1 block and a $1 reward
     */
    public static void create(GPlayer gPlayer, int treeBlocks, int reward) {
        OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
        if (offsetTable.getIncompleteBountyCount(gPlayer) >= MAX_BOUNTIES_CREATED_PER_PLAYER) {
            gPlayer.sendMsg(Lang.BOUNTY_MAXCREATED);
        } else if (treeBlocks < 1) {
            gPlayer.sendMsg(Lang.BOUNTY_BLOCKSREQUIRED);
        } else if (reward < 1) {
            gPlayer.sendMsg(Lang.BOUNTY_REWARDREQUIRED);
        } else {
            //Withdraw from account:
            boolean isWithdrawn = false;
            double balance = 0;
            Economy economy = GlobalWarming.getInstance().getEconomy();
            if (economy != null) {
                EconomyResponse response = economy.withdrawPlayer(gPlayer.getOfflinePlayer(), reward);
                isWithdrawn = response.transactionSuccess();
                balance = economy.getBalance(gPlayer.getOfflinePlayer());
            }

            if (isWithdrawn) {
                //New bounty:
                OffsetBounty bounty = new OffsetBounty(
                        GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE),
                        gPlayer.getUniqueId(),
                        null,
                        gPlayer.getAssociatedWorldId(),
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
                gPlayer.sendMsg(String.format(Lang.BOUNTY_CREATED.get(), balance));
            } else {
                //Notify:
                // - Not enough funds or economy plugin issue
                gPlayer.sendMsg(String.format(Lang.BOUNTY_NOTCREATED.get(), balance));
            }
        }
    }

    /**
     * Show all active bounties in the player's associated world
     * - Open bounties can be joined by any player (note: 1 player per bounty)
     */
    public static void show(GPlayer gPlayer) {
        ChatTable chatTable = new ChatTable(Lang.BOUNTY_TITLE.get());
        chatTable.setGridColor(ChatColor.BLUE);
        chatTable.addHeader(Lang.BOUNTY_PLAYER.get(), (int) (ChatTable.CHAT_WIDTH * 0.268));
        chatTable.addHeader(Lang.BOUNTY_HUNTER.get(), (int) (ChatTable.CHAT_WIDTH * 0.268));
        chatTable.addHeader(Lang.BOUNTY_BLOCKS.get(), (int) (ChatTable.CHAT_WIDTH * 0.232));
        chatTable.addHeader(Lang.BOUNTY_REWARD.get(), (int) (ChatTable.CHAT_WIDTH * 0.232));

        try {
            List<Integer> clickIds = new ArrayList<>();
            OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
            PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
            for (OffsetBounty offsetBounty : offsetTable.getOffsetList()) {
                //Ignore completed bounties and bounties not from the player's world:
                if (offsetBounty.getTimeCompleted() != 0 || !gPlayer.getAssociatedWorldId().equals(offsetBounty.getWorldId())) {
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

            String json = chatTable.toJson(
                    gPlayer,
                    Lang.BOUNTY_JOIN.get(),
                    "/gw bounty join",
                    Lang.BOUNTY_JOINTOOLTIP.get(),
                    clickIds);

            Bukkit.getServer().dispatchCommand(
                    Bukkit.getConsoleSender(),
                    json);
        } catch (Exception e) {
            gPlayer.sendMsg(Lang.BOUNTY_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Update the remaining blocks for an active bounty
     * - Requires a Vault-economy plugin to deposit funds
     */
    public static OffsetBounty update(GPlayer hunter, int blocksCompleted) {
        OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
        OffsetBounty bounty = offsetTable.update(hunter, blocksCompleted);
        if (bounty != null && bounty.getTimeCompleted() != 0) {
            Economy economy = GlobalWarming.getInstance().getEconomy();
            if (economy != null) {
                economy.depositPlayer(hunter.getOfflinePlayer(), bounty.getReward());
                notify(
                        bounty,
                        hunter,
                        String.format(Lang.BOUNTY_COMPLETEDBY.get(), hunter.getOfflinePlayer().getName()),
                        String.format(Lang.BOUNTY_COMPLETED.get(), bounty.getReward()));
            }
        }

        return bounty;
    }

    /**
     * Join a bounty
     * - Players are limited to joining one bounty
     * - Bounties are limited to one hunter
     */
    public static OffsetBounty join(GPlayer gPlayer, int bountyId) {
        OffsetBounty bounty = null;
        try {
            OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
            bounty = offsetTable.join(gPlayer, bountyId);
        } catch (Exception e) {
            if (e.getMessage().length() > 0) {
                gPlayer.sendMsg(e.getMessage());
            } else {
                e.printStackTrace();
            }
        }

        return bounty;
    }

    public static OffsetBounty unJoin(GPlayer gPlayer) {
        OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
        return offsetTable.unJoin(gPlayer);
    }

    public static void cancel(GPlayer gPlayer) {
        int refund = 0;
        OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
        List<OffsetBounty> cancelledBounties = offsetTable.cancel(gPlayer);
        Economy economy = GlobalWarming.getInstance().getEconomy();
        if (economy != null) {
            for (OffsetBounty bounty : cancelledBounties) {
                economy.depositPlayer(gPlayer.getOfflinePlayer(), bounty.getReward());
                refund += bounty.getReward();
            }
        }

        gPlayer.sendMsg(String.format(
                Lang.BOUNTY_CANCELLED.get(),
                cancelledBounties.size(),
                refund));
    }

    /**
     * Send messages to the creator and hunter of a bounty
     */
    public static void notify(OffsetBounty bounty, String creatorMessage, String hunterMessage) {
        PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
        UUID uuid = playerTable.getUuidMap().get(bounty.getHunterId());
        GPlayer hunter = playerTable.getPlayers().get(uuid);
        notify(bounty, hunter, creatorMessage, hunterMessage);
    }

    /**
     * Send messages to the creator and hunter of a bounty
     */
    public static void notify(OffsetBounty bounty, GPlayer hunter, String creatorMessage, String hunterMessage) {
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
}
