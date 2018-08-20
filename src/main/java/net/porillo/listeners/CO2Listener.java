package net.porillo.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.world.StructureGrowEvent;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.FurnaceTable;
import net.porillo.database.tables.TreeTable;
import net.porillo.objects.Contribution;
import net.porillo.objects.Furnace;
import net.porillo.objects.Location;
import net.porillo.objects.Player;
import net.porillo.objects.Reduction;
import net.porillo.objects.Tree;
import net.porillo.objects.World;

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
		Location location = new Location(event.getBlock().getLocation());
		World world = gw.getTableManager().getWorldTable().getWorld(location.getWorldName());
		FurnaceTable furnaceTable = gw.getTableManager().getFurnaceTable();
		
		if (furnaceTable.getLocationMap().containsKey(location)) {
			Furnace furnace = furnaceTable.getLocationMap().get(location);
			// Note: We hold the owner of the furnace responsible for emissions
			// If the furnace isn't protected, the furnace owner is still charged
			Player polluter = gw.getTableManager().getPlayerTable().getPlayers().get(furnace.getOwner().getUuid());
			Contribution emissions = gw.getConf().getEngine(world).furnaceBurn(polluter, event.getFuel());
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
		Location location = new Location(event.getLocation());
		World world = gw.getTableManager().getWorldTable().getWorld(location.getWorldName());
		TreeTable treeTable = gw.getTableManager().getTreeTable();
		
		if (treeTable.getLocationMap().containsKey(location)) {
			Tree tree = treeTable.getLocationMap().get(location);
			UUID ownerUUID = tree.getOwner().getUuid();
			Player planter = gw.getTableManager().getPlayerTable().getPlayers().get(ownerUUID);
			Reduction reduction = gw.getConf().getEngine(world).treeGrow(planter, event.getSpecies(), event.getBlocks());
			int carbonScore = planter.getCarbonScore();
			planter.setCarbonScore((int) (carbonScore - reduction.getReductionValue())); 
			
			// TODO: Queue planter score DB update
			// TODO: Queue new reduction DB insert
		} else {
			gw.getLogger().severe("Untracked structure grow occured:");
			gw.getLogger().severe("@ " + location.toString());
		}
	}
}
