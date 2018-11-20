package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.select.OffsetSelectQuery;
import net.porillo.database.queries.update.OffsetUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.engine.ClimateEngine;
import net.porillo.objects.GPlayer;
import net.porillo.objects.OffsetBounty;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OffsetTable extends Table implements SelectCallback<OffsetBounty> {

    /**
     * In memory storage of all available OffsetBounty
     * When an offset bounty is complete, delete from this list
     * On startup, query the offset table for available OffsetBounty's
     *
     * VAULT:
     * TODO: disable offsets if the VAULT plugin is not found
     * TODO: hold any money spent on a bounty
     * TODO: transfer money once a bounty is complete
     *
     * CLEANUP:
     * TODO: currently the bounties are marked as completed when
     * TODO: complete or cancelled, but should be probably be
     * TODO: deleted to reduce overhead, "synchronize" will be
     * TODO: required to protect critical sections during those
     * TODO: deletes
     */
    @Getter
    private List<OffsetBounty> offsetList = new ArrayList<>();

    public OffsetTable() {
        super("offsets");
        createIfNotExists();

        OffsetSelectQuery selectQuery = new OffsetSelectQuery(this);
        AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
    }

    public void addOffset(OffsetBounty bounty) {
        offsetList.add(bounty);
    }

    /**
     * Check if the player is already hunting a bounty
     * - Only consider incomplete bounties from the player's world
     */
    private boolean isPlayerHunting(GPlayer gPlayer) {
        boolean isPlayerHunting = false;
        String associatedWorldName = ClimateEngine.getInstance().getAssociatedWorldName(gPlayer.getPlayer());
        for (OffsetBounty bounty : offsetList) {
            if (bounty.getTimeCompleted() == 0 &&
                  bounty.getWorldName().equals(associatedWorldName) &&
                  bounty.getHunterId() != null &&
                  bounty.getHunterId().equals(gPlayer.getUniqueId())) {
                isPlayerHunting = true;
                break;
            }
        }

        return isPlayerHunting;
    }

    /**
     * Join a bounty
     * - Only consider incomplete bounties from the player's world
     * - One bounty per player and one player per bounty
     * - Cannot join your own bounty
     */
    public OffsetBounty join(GPlayer gPlayer, int bountyId) throws Exception {
        if (isPlayerHunting(gPlayer)) {
            throw new Exception(Lang.BOUNTY_ALREADYHUNTING.get());
        }

        OffsetBounty bounty = null;
        String associatedWorldName = ClimateEngine.getInstance().getAssociatedWorldName(gPlayer.getPlayer());
        for (int i = 0; i < offsetList.size(); i++) {
            if (offsetList.get(i).getTimeCompleted() == 0 &&
                  offsetList.get(i).getWorldName().equals(associatedWorldName) &&
                  offsetList.get(i).getUniqueId() == bountyId) {
                if (offsetList.get(i).getCreatorId() != null &&
                      offsetList.get(i).getCreatorId().equals(gPlayer.getUniqueId())) {
                    throw new Exception(Lang.BOUNTY_BOUNTYOWNER.get());
                } else if (offsetList.get(i).getHunterId() != null &&
                      offsetList.get(i).getHunterId() > 0) {
                    throw new Exception(Lang.BOUNTY_ANOTHERPLAYER.get());
                } else {
                    //Local (need a reference, not a copy):
                    offsetList.get(i).setHunterId(gPlayer.getUniqueId());
                    offsetList.get(i).setTimeStarted(System.currentTimeMillis());

                    //Database:
                    OffsetUpdateQuery updateQuery = new OffsetUpdateQuery(offsetList.get(i));
                    AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
                    bounty = offsetList.get(i);
                }

                break;
            }
        }

        if (bounty == null) {
            throw new Exception(Lang.BOUNTY_NOTFOUND.get());
        }

        return bounty;
    }

    /**
     * Leave the current bounty
     */
    public OffsetBounty unJoin(GPlayer gPlayer) {
        OffsetBounty bounty = null;
        String associatedWorldName = ClimateEngine.getInstance().getAssociatedWorldName(gPlayer.getPlayer());
        for (int i = 0; i < offsetList.size(); i++) {
            if (offsetList.get(i).getTimeCompleted() == 0 &&
                  offsetList.get(i).getWorldName().equals(associatedWorldName) &&
                  offsetList.get(i).getHunterId() != null &&
                  offsetList.get(i).getHunterId().equals(gPlayer.getUniqueId())) {
                //Local (need a reference, not a copy):
                offsetList.get(i).setTimeStarted(0);
                offsetList.get(i).setHunterId(null);

                //Database:
                OffsetUpdateQuery updateQuery = new OffsetUpdateQuery(offsetList.get(i));
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
                bounty = offsetList.get(i);
                break;
            }
        }

        return bounty;
    }

    /**
     * Cancel any idle bounties created by this player
     */
    public int cancel(GPlayer gPlayer) {
        int cancelledBounties = 0;
        String associatedWorldName = ClimateEngine.getInstance().getAssociatedWorldName(gPlayer.getPlayer());
        for (int i = 0; i < offsetList.size(); i++) {
            if (offsetList.get(i).getTimeCompleted() == 0 &&
                  offsetList.get(i).getWorldName().equals(associatedWorldName) &&
                  offsetList.get(i).getCreatorId() != null &&
                  offsetList.get(i).getCreatorId().equals(gPlayer.getUniqueId()) &&
                  (offsetList.get(i).getHunterId() == null ||
                  offsetList.get(i).getHunterId() == 0)) {
                //Local (need a reference, not a copy):
                offsetList.get(i).setTimeCompleted(System.currentTimeMillis());

                //Database:
                OffsetUpdateQuery updateQuery = new OffsetUpdateQuery(offsetList.get(i));
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
                cancelledBounties++;
            }
        }

        return cancelledBounties;
    }

    /**
     * Decrement the remaining-block count on a hunter's bounty
     * - Only consider incomplete bounties from the player's world
     */
    public OffsetBounty update(GPlayer gPlayer, int blocksCompleted) {
        OffsetBounty bounty = null;
        String associatedWorldName = ClimateEngine.getInstance().getAssociatedWorldName(gPlayer.getPlayer());
        for (int i = 0; i < offsetList.size(); i++) {
            if (offsetList.get(i).getTimeCompleted() == 0 &&
                  offsetList.get(i).getWorldName().equals(associatedWorldName) &&
                  offsetList.get(i).getHunterId() != null &&
                  offsetList.get(i).getHunterId().equals(gPlayer.getUniqueId())) {
                //Local (need a reference, not a copy):
                int blocksRemaining = Math.max(offsetList.get(i).getLogBlocksTarget() - blocksCompleted, 0);
                offsetList.get(i).setLogBlocksTarget(blocksRemaining);
                if (blocksRemaining == 0) {
                    //TODO: payout the reward
                    offsetList.get(i).setTimeCompleted(System.currentTimeMillis());
                }

                //Database:
                OffsetUpdateQuery updateQuery = new OffsetUpdateQuery(offsetList.get(i));
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
                bounty = offsetList.get(i);
                break;
            }
        }

        return bounty;
    }

    @Override
    public void onSelectionCompletion(List<OffsetBounty> returnList) throws SQLException {
        if (GlobalWarming.getInstance() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    offsetList.addAll(returnList);
                }
            }.runTask(GlobalWarming.getInstance());
        } else {
            System.out.printf("Selection returned %d offsets.%n", returnList.size());
        }
    }

    /**
     * Determine the amount of bounties created by the given player
     * - Only consider incomplete bounties from the player's world
     */
    public int getIncompleteBountyCount(GPlayer gPlayer) {
        int bountyCount = 0;
        String associatedWorldName = ClimateEngine.getInstance().getAssociatedWorldName(gPlayer.getPlayer());
        for (OffsetBounty bounty : offsetList) {
            if (bounty.getTimeCompleted() == 0 &&
                  bounty.getWorldName().equals(associatedWorldName) &&
                  bounty.getCreatorId() != null &&
                  bounty.getCreatorId().equals(gPlayer.getUniqueId())) {
                bountyCount++;
            }
        }

        return bountyCount;
    }
}
