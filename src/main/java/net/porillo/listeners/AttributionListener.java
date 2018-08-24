package net.porillo.listeners;

import net.porillo.GlobalWarming;
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
import java.util.UUID;

public class AttributionListener implements Listener {

	private GlobalWarming gw;

	public AttributionListener(GlobalWarming globalWarming) {
		this.gw = globalWarming;
	}

	/**
	 * Relate placed blocks to players
	 * @param event block place event
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() != Material.FURNACE && !event.getBlockPlaced().getType().name().endsWith("SAPLING")) {
			return;
		}

		Location location = event.getBlockPlaced().getLocation();
		PlayerTable playerTable = gw.getTableManager().getPlayerTable();
		GPlayer player;

		if (playerTable.getPlayers().containsKey(event.getPlayer().getUniqueId())) {
			player = playerTable.getPlayers().get(event.getPlayer().getUniqueId());
		} else {
			player = new GPlayer(event.getPlayer().getUniqueId(), System.currentTimeMillis(), 0);
		}
		
		if (event.getBlockPlaced().getType() == Material.FURNACE) {
			// Record furnace placement to track GPlayer attribution
			FurnaceTable furnaceTable = gw.getTableManager().getFurnaceTable();
			Map<GPlayer, HashSet<Furnace>> playerFurnaceMap = furnaceTable.getPlayerMap();
			
			// Create new furnace object 
			Furnace furnace = new Furnace(UUID.randomUUID(), player, location);

			if (furnaceTable.getLocationMap().containsKey(location)) {
				gw.getLogger().severe("Furnace placed at location of existing furnace!");
				gw.getLogger().severe("@ " + location.toString());
				return;
			}

			if (playerFurnaceMap.containsKey(player)) {
				HashSet<Furnace> furnaces = playerFurnaceMap.get(player);
				furnaces.add(furnace);
				playerFurnaceMap.put(player, furnaces);
			} else {
				HashSet<Furnace> furnaces = new HashSet<>();
				furnaces.add(furnace);
				playerFurnaceMap.put(player, furnaces);
			}

			furnaceTable.getLocationMap().put(location, furnace);
			// TODO: Queue Furnace Insert
			
		} else if (event.getBlockPlaced().getType().name().endsWith("SAPLING")) {
			TreeTable treeTable = gw.getTableManager().getTreeTable();
			// Track saplings to give credit to those when their saplings grow
			// Saplings are trees of size 0
			Tree tree = new Tree(UUID.randomUUID(), player, location, true, 0);

			if (treeTable.getLocationMap().containsKey(location)) {
				gw.getLogger().severe("Tree placed at location of existing tree!");
				gw.getLogger().severe("@ " + location.toString());
				return;
			}

			if (treeTable.getPlayerMap().containsKey(player)) {
				HashSet<Tree> trees = treeTable.getPlayerMap().get(player);
				trees.add(tree);
				treeTable.getPlayerMap().put(player, trees);
			} else {
				HashSet<Tree> trees = new HashSet<>();
				trees.add(tree);
				treeTable.getPlayerMap().put(player, trees);
			}

			treeTable.getLocationMap().put(location, tree);
			// TODO: Queue Tree Insert
		}
	}

	/**
	 * Flag furnace as removed
	 * @param event
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Location location = event.getBlock().getLocation();

		if (event.getBlock().getType() == Material.FURNACE) {
			FurnaceTable furnaceTable = gw.getTableManager().getFurnaceTable();
			Map<GPlayer, HashSet<Furnace>> playerFurnaceMap = furnaceTable.getPlayerMap();

			if (furnaceTable.getLocationMap().containsKey(location)) {
				Furnace furnace = furnaceTable.getLocationMap().get(location);
				final GPlayer owner = furnace.getOwner();
				
				HashSet<Furnace> furnaces = playerFurnaceMap.get(owner);
				furnaces.remove(furnace);
				playerFurnaceMap.put(owner, furnaces);
				// TODO: Queue Furnace Delete
			} else {
				gw.getLogger().info("Untracked furnace destroyed. @ " + location.toString());
			}
		} else if (event.getBlock().getType().name().endsWith("SAPLING")) {
			TreeTable treeTable = gw.getTableManager().getTreeTable();
			Map<GPlayer, HashSet<Tree>> playerTreeMap = treeTable.getPlayerMap();

			if (treeTable.getLocationMap().containsKey(location)) {
				Tree tree = treeTable.getLocationMap().get(location);
				final GPlayer owner = tree.getOwner();

				HashSet<Tree> trees = playerTreeMap.get(owner);
				trees.remove(tree);
				playerTreeMap.put(owner, trees);
				// TODO: Queue Tree Delete
			} else {
				gw.getLogger().info("Untracked furnace destroyed. @ " + location.toString());
			}
		}
	}
}
