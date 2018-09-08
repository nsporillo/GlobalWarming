package net.porillo.engine;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.WorldTable;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GWorld;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClimateEngine {

	private static ClimateEngine climateEngine;

	private Map<String, WorldClimateEngine> worldClimateEngines;

	public ClimateEngine() {
		this.worldClimateEngines = new HashMap<>();
	}

	public void loadWorldClimateEngines(List<String> enabledWorlds) {
		for (String world : enabledWorlds) {
			GlobalWarming.getInstance().getLogger().info("Loading Climate Engine for " + world);
			worldClimateEngines.put(world, new WorldClimateEngine(world));

			WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();
			GWorld gworld = worldTable.getWorld(world);

			// Delayed attempt create the world object if it doesn't currently exist
			if (gworld == null) {
				new BukkitRunnable() {
					@Override
					public void run() {
						GWorld gw = worldTable.getWorld(world);

						if (gw == null) {
							worldTable.insertNewWorld(world);
						}
					}
				}.runTaskLater(GlobalWarming.getInstance(), 40L);
			}
		}
	}

	public WorldClimateEngine getClimateEngine(String worldName) {
		if (worldClimateEngines.containsKey(worldName)) {
			return worldClimateEngines.get(worldName);
		}

		throw new NullPointerException("Climate Engine Not found for World -> " + worldName);
	}

	public boolean hasClimateEngine(String worldName) {
		return getClimateEngine(worldName) != null;
	}

	public static ClimateEngine getInstance() {
		if (climateEngine == null) {
			climateEngine = new ClimateEngine();
		}

		return climateEngine;
	}
}
