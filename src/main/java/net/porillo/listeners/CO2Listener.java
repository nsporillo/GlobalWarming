package net.porillo.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class CO2Listener implements Listener {

	/**
	 * Detect when CO2 is emitted via furnace
	 * @param event furnace burn
	 */
	@EventHandler
	public void onFurnaceSmelt(FurnaceBurnEvent event) {

	}

	/**
	 * Detect when CO2 is absorbed via new tree
	 * @param event structure grow event (tree grow)
	 */
	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {

	}
}
