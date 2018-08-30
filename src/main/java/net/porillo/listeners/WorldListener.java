package net.porillo.listeners;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.WorldTable;
import net.porillo.objects.GWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

public class WorldListener implements Listener {

	private GlobalWarming gw;

	public WorldListener(GlobalWarming main) {
		this.gw = main;
	}


	@EventHandler
	public void onChunkPopulation(ChunkPopulateEvent event) {
		WorldTable worldTable = gw.getTableManager().getWorldTable();
		// TODO: Optimize this lookup
		GWorld world = worldTable.getWorld(event.getWorld().getName());

		if (world != null) {
			int size = world.getSize();
			world.setSize(size + 1);
			// TODO: Trigger temperature adjustment? 
		}
	}
}
