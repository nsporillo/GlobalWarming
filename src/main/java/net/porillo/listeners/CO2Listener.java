package net.porillo.listeners;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.FurnaceTable;
import net.porillo.database.tables.TreeTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.objects.*;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.UUID;

public class CO2Listener implements Listener {

	private GlobalWarming gw;
	
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

		if (furnaceTable.getLocationMap().containsKey(location)) {
			Long uuid = furnaceTable.getLocationMap().get(location);
			Furnace furnace = furnaceTable.getFurnaceMap().get(uuid);
			// Note: We hold the owner of the furnace responsible for emissions
			// If the furnace isn't protected, the furnace owner is still charged
			GPlayer polluter = gw.getTableManager().getPlayerTable().getPlayers().get(furnace.getOwner().getUuid());
			Contribution emissions = ClimateEngine.getInstance().getClimateEngine(world.getWorldName()).furnaceBurn(polluter, event.getFuel());
			int carbonScore = polluter.getCarbonScore();
			polluter.setCarbonScore((int) (carbonScore + emissions.getContributionValue()));
			
			// TODO: Queue polluter score DB update
			// TODO: Queue new contribution DB insert
		} else {
			gw.getLogger().severe(event.getFuel().getType().name() + " burned as fuel in an untracked furnace!");
			gw.getLogger().severe("@ " + location.toString());
		}
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

		if (treeTable.getLocationMap().containsKey(location)) {
			Long uuid = treeTable.getLocationMap().get(location);
			Tree tree = treeTable.getTreeMap().get(uuid);
			UUID ownerUUID = tree.getOwner().getUuid();
			GPlayer planter = gw.getTableManager().getPlayerTable().getPlayers().get(ownerUUID);
			Reduction reduction = ClimateEngine.getInstance().getClimateEngine(world.getWorldName()).treeGrow(planter, event.getSpecies(), event.getBlocks());
			int carbonScore = planter.getCarbonScore();
			planter.setCarbonScore((int) (carbonScore - reduction.getReductionValue())); 

			tree.setSapling(false);

			// TODO: Queue tree DB update
			// TODO: Queue planter score DB update
			// TODO: Queue new reduction DB insert
		} else {
			gw.getLogger().severe("Untracked structure grow occured:");
			gw.getLogger().severe("@ " + location.toString());
		}
	}
}
