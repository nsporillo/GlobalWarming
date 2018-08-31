package net.porillo.engine;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.WorldTable;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GWorld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClimateEngine {

	private static ClimateEngine climateEngine;

	private Map<String, WorldClimateEngine> worldClimateEngines = new HashMap<>();

	public void loadWorldClimateEngines(List<String> enabledWorlds) {
		for (String world : enabledWorlds) {
			WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();

			GWorld gWorld = worldTable.getWorld(world);
			if (gWorld == null) {
				gWorld = worldTable.insertNewWorld(world);
			}

			worldClimateEngines.put(world, new WorldClimateEngine(gWorld));
		}
	}

	public WorldClimateEngine getClimateEngine(String worldName) {
		if (worldClimateEngines.containsKey(worldName)) {
			return worldClimateEngines.get(worldName);
		}

		return null;
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
