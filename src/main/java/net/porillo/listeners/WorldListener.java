package net.porillo.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.WorldLoadEvent;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.WorldTable;
import net.porillo.objects.World;

public class WorldListener implements Listener {

	private GlobalWarming gw;

	public WorldListener(GlobalWarming main) {
		this.gw = main;
	}

	private World initializeNewWorld(org.bukkit.World world) {
		World newWorld = new World();
		newWorld.setWorldName(world.getName());
		newWorld.setScore(0);
		newWorld.setAge(System.currentTimeMillis());
		newWorld.setSize(world.getLoadedChunks().length);
		return newWorld;
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		WorldTable worldTable = gw.getTableManager().getWorldTable();

		// World is not in our database, create new world object
		if (worldTable.getWorld(event.getWorld().getName()) == null) {
			worldTable.addWorld(initializeNewWorld(event.getWorld()));
		}

	}

	@EventHandler
	public void onChunkPopulation(ChunkPopulateEvent event) {
		WorldTable worldTable = gw.getTableManager().getWorldTable();
		World world = worldTable.getWorld(event.getWorld().getName());

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
