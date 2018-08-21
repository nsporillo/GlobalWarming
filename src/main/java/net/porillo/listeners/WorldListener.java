package net.porillo.listeners;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.WorldTable;
import net.porillo.objects.GWorld;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

	private GlobalWarming gw;

	public WorldListener(GlobalWarming main) {
		this.gw = main;
	}

	private GWorld initializeNewWorld(World world) {
		GWorld newWorld = new GWorld();
		newWorld.setWorldName(world.getName());
		
		newWorld.setFirstSeen(System.currentTimeMillis());
		newWorld.setSize(world.getLoadedChunks().length);
		return newWorld;
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		WorldTable worldTable = gw.getTableManager().getWorldTable();

		// GWorld is not in our database, create new world object
		if (worldTable.getWorld(event.getWorld().getName()) == null) {
			worldTable.addWorld(initializeNewWorld(event.getWorld()));
		}

	}

	@EventHandler
	public void onChunkPopulation(ChunkPopulateEvent event) {
		WorldTable worldTable = gw.getTableManager().getWorldTable();
		GWorld world = worldTable.getWorld(event.getWorld().getName());

		if (world != null) {
			int size = world.getSize();
			world.setSize(size + 1);
			// TODO: Trigger temperature adjustment? 
		} else {
			worldTable.addWorld(initializeNewWorld(event.getWorld()));
			onChunkPopulation(event); // dont skip the chunk population
		}
	}
}
