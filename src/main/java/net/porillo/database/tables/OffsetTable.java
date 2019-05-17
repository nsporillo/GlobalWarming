package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.select.OffsetSelectQuery;
import net.porillo.database.queries.update.OffsetUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GPlayer;
import net.porillo.objects.OffsetBounty;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class OffsetTable extends Table implements SelectCallback<OffsetBounty> {

    /**
     * In memory storage of all available OffsetBounty
     * TODO: When an offset bounty is complete, delete from this list
     * TODO: "synchronize" will be required to protect critical
     */
    @Getter private List<OffsetBounty> offsetList = new ArrayList<>();

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
        for (OffsetBounty bounty : offsetList) {
            if (bounty.getTimeCompleted() == 0 &&
                  bounty.getWorldId().equals(gPlayer.getAssociatedWorldId()) &&
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

        OffsetBounty joinedBounty = null;
        for (OffsetBounty bounty : offsetList) {
            if (bounty.getTimeCompleted() == 0 &&
                  bounty.getWorldId().equals(gPlayer.getAssociatedWorldId()) &&
                  bounty.getUniqueId() == bountyId) {
                if (bounty.getCreatorId() != null &&
                      bounty.getCreatorId().equals(gPlayer.getUniqueId())) {
                    throw new Exception(Lang.BOUNTY_BOUNTYOWNER.get());
                }

                if (bounty.getHunterId() != null && bounty.getHunterId() > 0) {
                    throw new Exception(Lang.BOUNTY_ANOTHERPLAYER.get());
                }

                //Bounty accepted:
                bounty.setHunterId(gPlayer.getUniqueId());
                bounty.setTimeStarted(System.currentTimeMillis());

                //Database update:
                OffsetUpdateQuery updateQuery = new OffsetUpdateQuery(bounty);
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
                joinedBounty = bounty;
                break;
            }
        }

        if (joinedBounty == null) {
            throw new Exception(Lang.BOUNTY_NOTFOUND.get());
        }

        return joinedBounty;
    }

    /**
     * Leave the current bounty
     */
    public OffsetBounty unJoin(GPlayer gPlayer) {
        OffsetBounty abandonedBounty = null;
        for (OffsetBounty bounty : offsetList) {
            if (bounty.getTimeCompleted() == 0 &&
                  bounty.getWorldId().equals(gPlayer.getAssociatedWorldId()) &&
                  bounty.getHunterId() != null &&
                  bounty.getHunterId().equals(gPlayer.getUniqueId())) {
                //Bounty abandoned:
                bounty.setTimeStarted(0);
                bounty.setHunterId(null);

                //Database update:
                OffsetUpdateQuery updateQuery = new OffsetUpdateQuery(bounty);
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
                abandonedBounty = bounty;
                break;
            }
        }

        return abandonedBounty;
    }

    /**
     * Cancel any idle bounties created by this player
     */
    public List<OffsetBounty> cancel(GPlayer gPlayer) {
        List<OffsetBounty> cancelledBounties = new ArrayList<>();
        for (OffsetBounty bounty : offsetList) {
            if (bounty.getTimeCompleted() == 0 &&
                  bounty.getWorldId().equals(gPlayer.getAssociatedWorldId()) &&
                  bounty.getCreatorId() != null &&
                  bounty.getCreatorId().equals(gPlayer.getUniqueId()) &&
                  (bounty.getHunterId() == null ||
                        bounty.getHunterId() == 0)) {
                //Bounty cancelled:
                bounty.setTimeCompleted(System.currentTimeMillis());

                //Database update:
                OffsetUpdateQuery updateQuery = new OffsetUpdateQuery(bounty);
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
                cancelledBounties.add(bounty);
            }
        }

        return cancelledBounties;
    }

    /**
     * Decrement the remaining-block count on a hunter's bounty
     * - Only consider incomplete bounties from the player's world
     */
    public OffsetBounty update(GPlayer gPlayer, int blocksCompleted) {
        OffsetBounty updatedBounty = null;
        for (OffsetBounty bounty : offsetList) {
            if (bounty.getTimeCompleted() == 0 &&
                  bounty.getWorldId().equals(gPlayer.getAssociatedWorldId()) &&
                  bounty.getHunterId() != null &&
                  bounty.getHunterId().equals(gPlayer.getUniqueId())) {
                //Update the blocks remaining:
                int blocksRemaining = Math.max(bounty.getLogBlocksTarget() - blocksCompleted, 0);
                bounty.setLogBlocksTarget(blocksRemaining);
                if (blocksRemaining == 0) {
                    bounty.setTimeCompleted(System.currentTimeMillis());
                }

                //Database update:
                OffsetUpdateQuery updateQuery = new OffsetUpdateQuery(bounty);
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
                updatedBounty = bounty;
                break;
            }
        }

        return updatedBounty;
    }

    @Override
    public void onSelectionCompletion(List<OffsetBounty> returnList) {
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
        for (OffsetBounty bounty : offsetList) {
            if (bounty.getTimeCompleted() == 0 &&
                  bounty.getWorldId().equals(gPlayer.getAssociatedWorldId()) &&
                  bounty.getCreatorId() != null &&
                  bounty.getCreatorId().equals(gPlayer.getUniqueId())) {
                bountyCount++;
            }
        }

        return bountyCount;
    }
}
