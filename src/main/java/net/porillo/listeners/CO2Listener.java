package net.porillo.listeners;

import net.porillo.GlobalWarming;
import net.porillo.database.queries.insert.ContributionInsertQuery;
import net.porillo.database.queries.insert.FurnaceInsertQuery;
import net.porillo.database.queries.insert.ReductionInsertQuery;
import net.porillo.database.queries.insert.TreeInsertQuery;
import net.porillo.database.queries.update.PlayerUpdateQuery;
import net.porillo.database.queries.update.TreeUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.FurnaceTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.database.tables.TreeTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.objects.*;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.FurnaceInventory;

import java.util.UUID;

public class CO2Listener implements Listener {

	private GlobalWarming gw;

	private static final UUID untrackedUUID = UUID.fromString("1-1-1-1-1");
	
	public CO2Listener(GlobalWarming main) {
		this.gw = main;
	}
	
	/**
	 * Detect when CO2 is emitted via furnace
	 * @param event furnace burn
	 */
	@EventHandler
	public void onFurnaceSmelt(FurnaceBurnEvent event) {
		Location location = event.getBlock().getLocation();
		GWorld world = gw.getTableManager().getWorldTable().getWorld(location.getWorld().getName());
		FurnaceTable furnaceTable = gw.getTableManager().getFurnaceTable();
		GPlayer polluter;

		if (furnaceTable.getLocationMap().containsKey(location)) {
			Furnace furnace = furnaceTable.getFurnaceMap().get(furnaceTable.getLocationMap().get(location));
			polluter = furnace.getOwner(); // whoever placed the furnace is charged.
		} else {
			/*
			 * This might happen if a player has a redstone hopper setup that feeds untracked furnaces
			 * In this case, just consider it to be untracked emissions.
			 */
			PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
			polluter = playerTable.getOrCreatePlayer(untrackedUUID, true);

			// First create a new furnace object and store it
			Long uniqueId = GlobalWarming.getInstance().getRandom().nextLong();
			Furnace furnace = new Furnace(uniqueId, polluter, location, true);

			// Update all furnace collections
			furnaceTable.updateFurnace(furnace);

			// Create a new furnace insert query and queue it
			FurnaceInsertQuery insertQuery = new FurnaceInsertQuery(furnace);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

			gw.getLogger().warning(event.getFuel().getType().name() + " burned as fuel in an untracked furnace!");
			gw.getLogger().warning("@ " + location.toString());
		}

		// Create a contribution object using this worlds climate engine
		Contribution contrib = ClimateEngine.getInstance().getClimateEngine(world.getWorldName()).furnaceBurn(polluter, event.getFuel());
		int carbonScore = polluter.getCarbonScore();
		polluter.setCarbonScore((int) (carbonScore + contrib.getContributionValue()));

		// Queue an update to the player table
		PlayerUpdateQuery updateQuery = new PlayerUpdateQuery(polluter);
		AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);

		// Queue an insert into the contributions table
		ContributionInsertQuery insertQuery = new ContributionInsertQuery(contrib);
		AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
	}

	/**
	 * Detect when CO2 is absorbed via new tree
	 * @param event structure grow event (tree grow)
	 */
	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {
		Location location = event.getLocation();
		GWorld world = gw.getTableManager().getWorldTable().getWorld(location.getWorld().getName());
		TreeTable treeTable = gw.getTableManager().getTreeTable();
		GPlayer planter;

		// TODO: Add TreeType species to reduction model

		if (treeTable.getLocationMap().containsKey(location)) {
			Tree tree = treeTable.getTreeMap().get(treeTable.getLocationMap().get(location));
			planter = tree.getOwner();

			Reduction reduction = ClimateEngine.getInstance().getClimateEngine(world.getWorldName()).treeGrow(planter, event.getSpecies(), event.getBlocks());
			int carbonScore = planter.getCarbonScore();
			planter.setCarbonScore((int) (carbonScore - reduction.getReductionValue()));

			tree.setSapling(false);
			tree.setSize(event.getBlocks().size()); // TODO: Only consider core species blocks as tree size

			// Queue tree update query
			TreeUpdateQuery treeUpdateQuery = new TreeUpdateQuery(tree);
			AsyncDBQueue.getInstance().queueUpdateQuery(treeUpdateQuery);
		} else {
			PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
			planter = playerTable.getOrCreatePlayer(untrackedUUID, true);

			// First create a new tree object and store it
			Long uniqueId = GlobalWarming.getInstance().getRandom().nextLong();
			// TODO: Only consider core species blocks as tree size
			Tree tree = new Tree(uniqueId, planter, location, false, event.getBlocks().size());

			TreeInsertQuery insertQuery = new TreeInsertQuery(tree);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

			gw.getLogger().warning("Untracked structure grow occured:");
			gw.getLogger().warning("@ " + location.toString());
		}

		// Create a new reduction object using the worlds climate engine
		Reduction reduction = ClimateEngine.getInstance().getClimateEngine(world.getWorldName()).treeGrow(planter, event.getSpecies(), event.getBlocks());
		int carbonScore = planter.getCarbonScore();
		planter.setCarbonScore((int) (carbonScore - reduction.getReductionValue()));

		// Queue player update query
		PlayerUpdateQuery playerUpdateQuery = new PlayerUpdateQuery(planter);
		AsyncDBQueue.getInstance().queueUpdateQuery(playerUpdateQuery);

		// Queue reduction insert query
		ReductionInsertQuery insertQuery = new ReductionInsertQuery(reduction);
		AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
	}

	// TODO: Track furnace smelts which occur in untracked furnaces
	// For now, we will simply use this to attribute contributions to players
	// In the future, we might want to use this to associate untracked furnaces
	// with players. If GW is installed on an existing map, then players might
	// never move their furnaces. So we can maybe define a number of smelts
	// until we consider the
	// @EventHandler
	public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
		if (event.getItem().getType().isFuel()) {
			if (event.getDestination() instanceof FurnaceInventory) {
				FurnaceInventory furnaceInventory = (FurnaceInventory) event.getDestination();
			}
		}
	}
}
