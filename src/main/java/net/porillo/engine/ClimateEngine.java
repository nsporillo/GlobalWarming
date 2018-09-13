package net.porillo.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.config.WorldConfig;
import net.porillo.database.tables.WorldTable;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClimateEngine {

	private static ClimateEngine climateEngine;

	private Map<String, WorldClimateEngine> worldClimateEngines;
	@Getter private Gson gson;

	public ClimateEngine() {
		this.worldClimateEngines = new HashMap<>();

		if (GlobalWarming.getInstance() != null) {
			this.gson = GlobalWarming.getInstance().getGson();
		} else {
			this.gson = new GsonBuilder().setPrettyPrinting().create();
		}
	}

	public void loadWorldClimateEngines() {
		Set<WorldConfig> worldConfigs = new HashSet<>();

		// Load a world config for all worlds
		for (World world : Bukkit.getWorlds()) {
			worldConfigs.add(new WorldConfig(world.getName()));
		}

		for (WorldConfig config : worldConfigs) {
			final String world = config.getWorld();

			if (config.isEnabled()) {
				GlobalWarming.getInstance().getLogger().info("Loading Climate Engine for " + world);
			} else {
				GlobalWarming.getInstance().getLogger().info(String.format("World '%s' found, but is disabled.", world));
			}

			worldClimateEngines.put(world, new WorldClimateEngine(config));

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

	public boolean isEffectEnabled(String worldName, ClimateEffectType type) {
		WorldClimateEngine climateEngine = getClimateEngine(worldName);
		return climateEngine != null && climateEngine.isEffectEnabled(type);
	}

	public static ClimateEngine getInstance() {
		if (climateEngine == null) {
			climateEngine = new ClimateEngine();
		}

		return climateEngine;
	}
}
