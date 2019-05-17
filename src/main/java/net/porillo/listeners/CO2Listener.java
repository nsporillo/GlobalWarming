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
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.*;
import org.bukkit.Location;
import org.bukkit.Material;
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
	@EventHandler(ignoreCancelled = true)
	public void onFurnaceSmelt(FurnaceBurnEvent event) {
		//Ignore if the block's world-climate is disabled:
		UUID worldId = event.getBlock().getWorld().getUID();
		WorldClimateEngine eventClimateEngine = ClimateEngine.getInstance().getClimateEngine(worldId);
		if (eventClimateEngine == null || !eventClimateEngine.isEnabled() || event.isCancelled()) {
			return;
		}

		//Setup:
		Location location = event.getBlock().getLocation();
		Material furnaceType = event.getBlock().getType();
		FurnaceTable furnaceTable = GlobalWarming.getInstance().getTableManager().getFurnaceTable();
		PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
		Furnace furnace = null;
		GPlayer polluter = null;
		UUID affectedWorldId = null;

		//Known furnaces:
		// - Use the player's associated world
		// - Supports offline players with active furnaces
		if (furnaceTable.getLocationMap().containsKey(location)) {
			furnace = (Furnace) furnaceTable.getBlockMap().get(furnaceTable.getLocationMap().get(location));
			UUID uuid = playerTable.getUuidMap().get(furnace.getOwnerId());
			polluter = playerTable.getPlayers().get(uuid);
			if (polluter != null) {
				affectedWorldId = polluter.getAssociatedWorldId();
			}
		}

		//Unknown furnaces:
		// - This might happen if a player has a redstone hopper setup that feeds untracked furnaces
		// - In this case, just consider it to be untracked emissions
		// - Get the existing untracked-player or create a new record otherwise
		// - Use the event's associated world (not the untracked player's world)
		// - NOTE: the untracked player is responsible for unknown furnaces from *all worlds*
		if (furnace == null) {
			polluter = playerTable.getOrCreatePlayer(untrackedUUID);
			affectedWorldId = eventClimateEngine.getConfig().getAssociatedWorldId();

			//Create a new furnace object:
			int uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
			furnace = new Furnace(uniqueId, polluter.getUniqueId(), location, true);

			//Update all furnace collections:
			furnaceTable.updateCollections(furnace);

			//Database update:
			FurnaceInsertQuery insertQuery = new FurnaceInsertQuery(furnace);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

			//Notification:
			gw.getLogger().warning(String.format("[%s] burned as fuel in an untracked furnace!", event.getFuel().getType().name()));
			gw.getLogger().warning(String.format("@ %s", location.toString()));
		}

		//Carbon updates:
		// - Record the contribution
		// - Update the associated world's carbon level
		// - Update the player's carbon score
		WorldClimateEngine affectedClimateEngine = ClimateEngine.getInstance().getClimateEngine(affectedWorldId);
		if (affectedClimateEngine != null && affectedClimateEngine.isEnabled()) {
			//Carbon contribution record:
			int contributionValue = 0;
			Contribution contribution = eventClimateEngine.furnaceBurn(furnace, furnaceType, event.getFuel());
			if (contribution != null) {
				//Queue an insert into the contributions table:
				ContributionInsertQuery insertQuery = new ContributionInsertQuery(contribution);
				AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
				contributionValue = contribution.getContributionValue();
			}

			//Polluter carbon scores:
			if (polluter != null) {
				//Increment the polluter's carbon score:
				int carbonScore = polluter.getCarbonScore();
				polluter.setCarbonScore(carbonScore + contributionValue);

				//Queue an update to the player table:
				PlayerUpdateQuery updateQuery = new PlayerUpdateQuery(polluter);
				AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
			}

			//Update the affected world's carbon levels:
			GlobalWarming.getInstance().getTableManager().getWorldTable().updateWorldCarbonValue(affectedWorldId, contributionValue);

			//Update the scoreboard:
			gw.getScoreboard().update(polluter);
		}
	}

	/**
	 * Detect when CO2 is absorbed via new tree
	 * - TODO: Add TreeType species to reduction model
	 * - TODO: Only consider core species blocks as tree size (see: setSize x2)
	 *
	 * @param event structure grow event (tree grow)
	 */
	@EventHandler(ignoreCancelled = true)
	public void onStructureGrow(StructureGrowEvent event) {
		//Ignore if the location's world-climate is disabled:
		UUID worldId = event.getLocation().getWorld().getUID();
		WorldClimateEngine eventClimateEngine = ClimateEngine.getInstance().getClimateEngine(worldId);
		if (eventClimateEngine == null || !eventClimateEngine.isEnabled() || event.isCancelled()) {
			return;
		}


		//Setup:
		Location location = event.getLocation();
		TreeTable treeTable = GlobalWarming.getInstance().getTableManager().getTreeTable();
		PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
		Tree tree = null;
		GPlayer planter = null;
		UUID affectedWorldId = null;

		//Known trees:
		// - Update the tree record
		// - Use the player's associated world
		// - Supports offline players with active saplings
		if (treeTable.getLocationMap().containsKey(location)) {
			//Tree update:
			tree = (Tree) treeTable.getBlockMap().get(treeTable.getLocationMap().get(location));
			tree.setSapling(false);
			tree.setSize(event.getBlocks().size());

			//Database update:
			TreeUpdateQuery treeUpdateQuery = new TreeUpdateQuery(tree);
			AsyncDBQueue.getInstance().queueUpdateQuery(treeUpdateQuery);

			//Affected world:
			UUID uuid = playerTable.getUuidMap().get(tree.getOwnerId());
			planter = playerTable.getPlayers().get(uuid);
			if (planter != null) {
				affectedWorldId = planter.getWorldId();
			}
		}

		//Unknown trees:
		if (tree == null) {
			planter = playerTable.getOrCreatePlayer(untrackedUUID);
			affectedWorldId = eventClimateEngine.getConfig().getAssociatedWorldId();

			//Create a new tree object:
			Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
			tree = new Tree(uniqueId, planter.getUniqueId(), location, false, event.getBlocks().size());

			//Update all tree collections:
			treeTable.updateCollections(tree);

			//Database update:
			TreeInsertQuery insertQuery = new TreeInsertQuery(tree);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

			//Notification:
			gw.getLogger().warning(String.format("Untracked growing structure: [%s]", event.getSpecies().name()));
			gw.getLogger().warning(String.format("@ %s", location.toString()));
		}

		//Carbon updates:
		// - Record the reduction
		// - Update the associated world's carbon level
		// - Update the player's carbon score
		WorldClimateEngine affectedClimateEngine = ClimateEngine.getInstance().getClimateEngine(affectedWorldId);
		if (affectedClimateEngine != null && affectedClimateEngine.isEnabled()) {
			//Carbon reduction record:
			Reduction reduction = eventClimateEngine.treeGrow(tree, event.getSpecies(), event.getBlocks());

			//Queue an insert into the contributions table:
			ReductionInsertQuery insertQuery = new ReductionInsertQuery(reduction);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
			int reductionValue = reduction.getReductionValue();

			//Carbon scores:
			// - When player's are bounty-hunting the affected player is the bounty-owner,
			//   not the tree-planter
			GPlayer affectedPlayer = planter;
			OffsetBounty updatedBounty = OffsetBounty.update(planter, event.getBlocks().size());
			if (updatedBounty != null) {
				UUID bountyCreator = playerTable.getUuidMap().get(updatedBounty.getCreatorId());
				affectedPlayer = playerTable.getPlayers().get(bountyCreator);
			}

			if (affectedPlayer != null) {
				//Increment the planter's carbon score:
				int carbonScore = affectedPlayer.getCarbonScore();
				affectedPlayer.setCarbonScore(carbonScore - reductionValue);

				//Queue an update to the player table:
				PlayerUpdateQuery updateQuery = new PlayerUpdateQuery(affectedPlayer);
				AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
			}

			//Update the affected world's carbon levels:
			GlobalWarming.getInstance().getTableManager().getWorldTable().updateWorldCarbonValue(affectedWorldId, -reductionValue);

			//Update the scoreboard:
			gw.getScoreboard().update(affectedPlayer);
		}
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
