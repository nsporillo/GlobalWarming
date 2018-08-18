package net.porillo.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class AttributionListener implements Listener {

	/**
	 * Relate placed blocks to players
	 * @param event block place event
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

	}

	/**
	 * Flag furnace as removed
	 * @param event
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

	}
}
