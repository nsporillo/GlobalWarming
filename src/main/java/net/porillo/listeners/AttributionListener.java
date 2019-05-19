package net.porillo.listeners;

import net.porillo.GlobalWarming;
import net.porillo.database.queries.insert.FurnaceInsertQuery;
import net.porillo.database.queries.insert.TreeInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.FurnaceTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.database.tables.TreeTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.objects.Furnace;
import net.porillo.objects.GPlayer;
import net.porillo.objects.Tree;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class AttributionListener implements Listener {

    private GlobalWarming gw;

    public AttributionListener(GlobalWarming globalWarming) {
        this.gw = globalWarming;
    }

    /**
     * Relate placed blocks to players
     *
     * @param event block place event
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material bType = event.getBlockPlaced().getType();

        //Ignore blocks that aren't furnaces or saplings:
        if ((bType != Material.FURNACE && bType != Material.BLAST_FURNACE && bType != Material.SMOKER) && !bType.name().endsWith("SAPLING")) {
            return;
        }

        //Ignore if the block's world-climate is disabled:
        if (!ClimateEngine.getInstance().isClimateEngineEnabled(event.getBlock().getWorld().getUID())) {
            return;
        }

        //Setup:
        Location location = event.getBlockPlaced().getLocation();
        PlayerTable playerTable = gw.getTableManager().getPlayerTable();
        GPlayer player = playerTable.getOrCreatePlayer(event.getPlayer().getUniqueId());
        Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);

        //Block handlers:
        if (bType == Material.FURNACE || bType == Material.BLAST_FURNACE || bType == Material.SMOKER) {
            //Furnaces:
            FurnaceTable furnaceTable = gw.getTableManager().getFurnaceTable();
            Furnace furnace = new Furnace(uniqueId, player.getUniqueId(), location, true);

            //Furnace already exists here:
            // - May happen if a furnace did not trigger onBlockBreak when destroyed
            // - Delete the old furnace at this location
            if (furnaceTable.deleteLocation(location) != null) {
                gw.getLogger().warning(String.format("Replacing furnace record @ %s", location.toString()));
            }

            //Update furnace collections with this new record:
            furnaceTable.updateCollections(furnace);

            //Database update:
            FurnaceInsertQuery insertQuery = new FurnaceInsertQuery(furnace);
            AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
        } else if (bType.name().endsWith("SAPLING")) {
            //Saplings:
            // - Tracked to credit their planter or bounty
            // - Saplings are trees of size 0
            TreeTable treeTable = gw.getTableManager().getTreeTable();
            Tree tree = new Tree(uniqueId, player.getUniqueId(), location, true, 0);

            //Furnace already exists here:
            // - May happen if a furnace did not trigger onBlockBreak when destroyed
            // - Saplings can burn down, etc. and get replanted in the same location
            // - Delete the old furnace at this location
            if (treeTable.deleteLocation(location) != null) {
                gw.getLogger().warning(String.format("Replacing sapling record @ %s", location.toString()));
            }

            //Update tree collections with this new sapling:
            treeTable.updateCollections(tree);

            //Database update:
            TreeInsertQuery insertQuery = new TreeInsertQuery(tree);
            AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
        }
    }

    /**
     * Set furnaces as inactive
     *
     * @param event block break event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material bType = event.getBlock().getType();

        //Ignore blocks that aren't furnaces or saplings:
        if ((bType != Material.FURNACE && bType != Material.BLAST_FURNACE && bType != Material.SMOKER) && !bType.name().endsWith("SAPLING")) {
            return;
        }

        //Ignore if the block's world-climate is disabled:
        if (!ClimateEngine.getInstance().isClimateEngineEnabled(event.getBlock().getWorld().getUID())) {
            return;
        }

        //Delete tracked records:
        Location location = event.getBlock().getLocation();
        if (bType == Material.FURNACE || bType == Material.BLAST_FURNACE || bType == Material.SMOKER) {
            //Furnace destroyed:
            // - Any "contribution" records based on a deleted furnace will
            //   no longer be able to look it up (this is OK, just be aware)
            FurnaceTable furnaceTable = gw.getTableManager().getFurnaceTable();
            if (furnaceTable.deleteLocation(location) == null) {
                gw.getLogger().info(String.format("Untracked furnace destroyed @ %s", location.toString()));
            }
        } else if (event.getBlock().getType().name().endsWith("SAPLING")) {
            //Sapling destroyed:
            // - Any "reduction" records based on a deleted sapling will
            //   no longer be able to look it up (this is OK, just be aware)
            TreeTable treeTable = gw.getTableManager().getTreeTable();
            if (treeTable.deleteLocation(location) == null) {
                gw.getLogger().info(String.format("Untracked sapling destroyed @ %s", location.toString()));
            }
        }
    }
}
