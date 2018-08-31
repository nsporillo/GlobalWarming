package net.porillo.listeners;

import net.porillo.GlobalWarming;
import net.porillo.database.queries.delete.TreeDeleteQuery;
import net.porillo.database.queries.insert.FurnaceInsertQuery;
import net.porillo.database.queries.insert.TreeInsertQuery;
import net.porillo.database.queries.update.FurnaceUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.FurnaceTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.database.tables.TreeTable;
import net.porillo.objects.Furnace;
import net.porillo.objects.GPlayer;
import net.porillo.objects.Tree;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Map;

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
		if (event.getBlockPlaced().getType() != Material.FURNACE && !event.getBlockPlaced().getType().name().endsWith("SAPLING")) {
			return;
		}

		Location location = event.getBlockPlaced().getLocation();
		PlayerTable playerTable = gw.getTableManager().getPlayerTable();
		GPlayer player = playerTable.getOrCreatePlayer(event.getPlayer().getUniqueId(), false);

		if (event.getBlockPlaced().getType() == Material.FURNACE) {
			// Record furnace placement to track GPlayer attribution
			FurnaceTable furnaceTable = gw.getTableManager().getFurnaceTable();

			// Create new furnace object
			Long uniqueId = GlobalWarming.getInstance().getRandom().nextLong();
			Furnace furnace = new Furnace(uniqueId, player, location, true);

			// Sanity check, if a furnace can be placed at a location which we think is a furnace then error out
			if (furnaceTable.getLocationMap().containsKey(location)) {
				gw.getLogger().severe("Furnace placed at location of existing furnace!");
				gw.getLogger().severe("@ " + location.toString());
				return;
			}

			// Update all furnace collections
			furnaceTable.updateFurnace(furnace);

			// Create a new furnace insert query and queue it
			FurnaceInsertQuery insertQuery = new FurnaceInsertQuery(furnace);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

		} else if (event.getBlockPlaced().getType().name().endsWith("SAPLING")) {
			TreeTable treeTable = gw.getTableManager().getTreeTable();
			// Track saplings to give credit to those when their saplings grow
			// Saplings are trees of size 0
			Long uniqueId = GlobalWarming.getInstance().getRandom().nextLong();
			Tree tree = new Tree(uniqueId, player, location, true, 0);

			if (treeTable.getLocationMap().containsKey(location)) {
				gw.getLogger().severe("Tree placed at location of existing tree!");
				gw.getLogger().severe("@ " + location.toString());
				return;
			}

			// Update all tree collections
			treeTable.updateTree(tree);

			// Create a new tree insert query and queue it
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
		Location location = event.getBlock().getLocation();

		if (event.getBlock().getType() == Material.FURNACE) {
			FurnaceTable furnaceTable = gw.getTableManager().getFurnaceTable();
			Map<GPlayer, HashSet<Long>> playerFurnaceMap = furnaceTable.getPlayerMap();

			if (furnaceTable.getLocationMap().containsKey(location)) {
				// Remove in-memory instances of this furnace
				final Long uniqueId = furnaceTable.getLocationMap().get(location);
				final Furnace furnace = furnaceTable.getFurnaceMap().get(uniqueId);
				final GPlayer owner = furnace.getOwner();

				// Remove furnace unique id from set of player owned furnaces
				HashSet<Long> furnaces = playerFurnaceMap.get(owner);
				furnaces.remove(uniqueId);
				playerFurnaceMap.put(owner, furnaces);

				// Remove map of location -> furnace unique id
				furnaceTable.getLocationMap().remove(location);
				// Remove map of furnace unique id -> furnace object
				furnaceTable.getFurnaceMap().remove(uniqueId);
				furnace.setActive(false);

				// We don't want to fully delete the furnace (this would create orphans)
				// Set the furnace to "inactive" in the database
				// This will mark the furnace to not be loaded back into memory next startup
				FurnaceUpdateQuery updateQuery = new FurnaceUpdateQuery(furnace);
				AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
				// TODO: Add DB triggers to delete inactive furnaces with no associated contributions
			} else {
				gw.getLogger().info("Untracked furnace destroyed. @ " + location.toString());
			}
		} else if (event.getBlock().getType().name().endsWith("SAPLING")) {
			TreeTable treeTable = gw.getTableManager().getTreeTable();
			Map<GPlayer, HashSet<Long>> playerTreeMap = treeTable.getPlayerMap();

			if (treeTable.getLocationMap().containsKey(location)) {
				final Long uniqueId = treeTable.getLocationMap().get(location);
				final Tree tree = treeTable.getTreeMap().get(uniqueId);
				final GPlayer owner = tree.getOwner();

				// Remove tree unique id from set of player owned trees
				HashSet<Long> trees = playerTreeMap.get(owner);
				trees.remove(uniqueId);
				playerTreeMap.put(owner, trees);

				// Remove map of location -> tree unique id
				treeTable.getLocationMap().remove(location);

				// Remove map of tree unique id -> tree object
				treeTable.getTreeMap().remove(uniqueId);
				// Since a tree only has one contribution associated with it (when it grows)
				// Delete the tree from the DB entirely
				TreeDeleteQuery deleteQuery = new TreeDeleteQuery(tree);
				AsyncDBQueue.getInstance().queueDeleteQuery(deleteQuery);
			} else {
				gw.getLogger().info("Untracked sapling destroyed. @ " + location.toString());
			}
		}
	}
}
