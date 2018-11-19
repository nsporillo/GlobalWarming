package net.porillo.listeners;

import net.porillo.GlobalWarming;
import net.porillo.commands.GeneralCommands;
import net.porillo.config.Lang;
import net.porillo.database.queries.insert.ContributionInsertQuery;
import net.porillo.database.queries.insert.FurnaceInsertQuery;
import net.porillo.database.queries.insert.ReductionInsertQuery;
import net.porillo.database.queries.insert.TreeInsertQuery;
import net.porillo.database.queries.update.PlayerUpdateQuery;
import net.porillo.database.queries.update.TreeUpdateQuery;
import net.porillo.database.queries.update.WorldUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.FurnaceTable;
import net.porillo.database.tables.OffsetTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.database.tables.TreeTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
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
	 *
	 * @param event furnace burn
	 */
	@EventHandler
	public void onFurnaceSmelt(FurnaceBurnEvent event) {
		// Don't handle events in worlds if it's disabled
		String eventWorldName = event.getBlock().getWorld().getName();
		if (!ClimateEngine.getInstance().isClimateEngineEnabled(eventWorldName)) {
			return;
		}

		WorldClimateEngine eventClimateEngine = ClimateEngine.getInstance().getClimateEngine(eventWorldName);
		Location location = event.getBlock().getLocation();
		FurnaceTable furnaceTable = GlobalWarming.getInstance().getTableManager().getFurnaceTable();
		PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
		Furnace furnace;
		GPlayer polluter;

		if (furnaceTable.getLocationMap().containsKey(location)) {
			furnace = furnaceTable.getFurnaceMap().get(furnaceTable.getLocationMap().get(location));
			UUID uuid = playerTable.getUuidMap().get(furnace.getOwnerID());

			// Exit if the player's associated-world climate engine is disabled:
			polluter = playerTable.getPlayers().get(uuid);
			if (!ClimateEngine.getInstance().isAssociatedEngineEnabled(polluter)) {
				return;
			}
		} else {
			/*
			 * This might happen if a player has a redstone hopper setup that feeds untracked furnaces
			 * In this case, just consider it to be untracked emissions.
			 */

			// Exit if the player's associated-world climate engine is disabled:
			polluter = playerTable.getOrCreatePlayer(untrackedUUID, true);
			if (!ClimateEngine.getInstance().isAssociatedEngineEnabled(polluter)) {
				return;
			}

			// First create a new furnace object and store it
			Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
			furnace = new Furnace(uniqueId, polluter.getUniqueId(), location, true);

			// Update all furnace collections
			furnaceTable.updateFurnace(furnace);

			// Create a new furnace insert query and queue it
			FurnaceInsertQuery insertQuery = new FurnaceInsertQuery(furnace);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

			gw.getLogger().warning(event.getFuel().getType().name() + " burned as fuel in an untracked furnace!");
			gw.getLogger().warning("@ " + location.toString());
		}

		// Update the carbon score in the associated world
		String associatedWorldName = eventClimateEngine.getConfig().getAssociation();
		GWorld associatedWorld = GlobalWarming.getInstance().getTableManager().getWorldTable().getWorld(associatedWorldName);
		Contribution contrib = eventClimateEngine.furnaceBurn(furnace, event.getFuel());

		// increment polluters carbon score
		int carbonScore = polluter.getCarbonScore();
		polluter.setCarbonScore(carbonScore + contrib.getContributionValue());

		//Update the scoreboard:
		gw.getScoreboard().update(polluter.getUuid());

		// increment worlds carbon score
		int carbon = associatedWorld.getCarbonValue();
		associatedWorld.setCarbonValue(carbon + contrib.getContributionValue());

		// Queue an update to the player table
		PlayerUpdateQuery updateQuery = new PlayerUpdateQuery(polluter);
		AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);

		// Queue an insert into the contributions table
		ContributionInsertQuery insertQuery = new ContributionInsertQuery(contrib);
		AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

		// Queue an update to the world table
		WorldUpdateQuery worldUpdateQuery = new WorldUpdateQuery(associatedWorld);
		AsyncDBQueue.getInstance().queueUpdateQuery(worldUpdateQuery);
	}

	/**
	 * Detect when CO2 is absorbed via new tree
	 *
	 * @param event structure grow event (tree grow)
	 */
	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {
		// Don't handle events in worlds if it's disabled
		String eventWorldName = event.getLocation().getWorld().getName();
		if (!ClimateEngine.getInstance().isClimateEngineEnabled(eventWorldName)) {
			return;
		}

		WorldClimateEngine eventClimateEngine = ClimateEngine.getInstance().getClimateEngine(eventWorldName);
		Location location = event.getLocation();
		TreeTable treeTable = GlobalWarming.getInstance().getTableManager().getTreeTable();
		PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
		Tree tree;
		GPlayer planter;

		// TODO: Add TreeType species to reduction model

		if (treeTable.getLocationMap().containsKey(location)) {
			tree = treeTable.getTreeMap().get(treeTable.getLocationMap().get(location));
			UUID uuid = playerTable.getUuidMap().get(tree.getOwnerID());

			// Exit if the player's associated-world climate engine is disabled:
			planter = playerTable.getPlayers().get(uuid);
			if (!ClimateEngine.getInstance().isAssociatedEngineEnabled(planter)) {
				return;
			}

			// Queue tree update query
			tree.setSapling(false);
			tree.setSize(event.getBlocks().size()); // TODO: Only consider core species blocks as tree size
			TreeUpdateQuery treeUpdateQuery = new TreeUpdateQuery(tree);
			AsyncDBQueue.getInstance().queueUpdateQuery(treeUpdateQuery);
		} else {
			// Exit if the player's associated-world climate engine is disabled:
			planter = playerTable.getOrCreatePlayer(untrackedUUID, true);
			if (!ClimateEngine.getInstance().isAssociatedEngineEnabled(planter)) {
				return;
			}

			// First create a new tree object and store it
			Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
			// TODO: Only consider core species blocks as tree size
			tree = new Tree(uniqueId, planter.getUniqueId(), location, false, event.getBlocks().size());

			TreeInsertQuery insertQuery = new TreeInsertQuery(tree);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

			gw.getLogger().warning("Untracked structure grow occurred:");
			gw.getLogger().warning("@ " + location.toString());
		}

		// Update the carbon score in the associated world
		String associatedWorldName = eventClimateEngine.getConfig().getAssociation();
		GWorld associatedWorld = GlobalWarming.getInstance().getTableManager().getWorldTable().getWorld(associatedWorldName);

		// Create a new reduction object using the worlds climate engine
		Reduction reduction = eventClimateEngine.treeGrow(tree, event.getSpecies(), event.getBlocks());

		// decrement players carbon score
		int carbonScore = planter.getCarbonScore();
		planter.setCarbonScore(carbonScore - reduction.getReductionValue());

		//Update bounties:
		OffsetTable offsetTable = gw.getTableManager().getOffsetTable();
		OffsetBounty bounty = offsetTable.update(planter, event.getBlocks().size());
		if (bounty != null && bounty.getTimeCompleted() != 0) {
			GeneralCommands.notifyBounty(
				bounty,
				planter,
				String.format(Lang.BOUNTY_COMPLETEDBY.get(), planter.getPlayer().getName()),
				String.format(Lang.BOUNTY_COMPLETED.get(), bounty.getReward()));
		}

		//Update the scoreboard:
		gw.getScoreboard().update(planter.getUuid());

		// decrement worlds carbon score
		int carbon = associatedWorld.getCarbonValue();
		associatedWorld.setCarbonValue(carbon - reduction.getReductionValue());

		// Queue player update query
		PlayerUpdateQuery playerUpdateQuery = new PlayerUpdateQuery(planter);
		AsyncDBQueue.getInstance().queueUpdateQuery(playerUpdateQuery);

		// Queue reduction insert query
		ReductionInsertQuery insertQuery = new ReductionInsertQuery(reduction);
		AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

		// Queue an update to the world table
		WorldUpdateQuery worldUpdateQuery = new WorldUpdateQuery(associatedWorld);
		AsyncDBQueue.getInstance().queueUpdateQuery(worldUpdateQuery);
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