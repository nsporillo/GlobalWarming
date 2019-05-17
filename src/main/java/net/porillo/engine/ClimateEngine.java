package net.porillo.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.config.WorldConfig;
import net.porillo.database.tables.WorldTable;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ClimateEngine {

    private static ClimateEngine climateEngine;
    private Map<UUID, WorldClimateEngine> worldClimateEngines;
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
        //Load a world config for all worlds:
        Set<WorldConfig> worldConfigs = new HashSet<>();
        for (World world : Bukkit.getWorlds()) {
            worldConfigs.add(new WorldConfig(world.getUID()));
        }

        for (WorldConfig config : worldConfigs) {
            //Engine status notification:
            final UUID worldId = config.getWorldId();
            final World world = Bukkit.getWorld(worldId);

            if (world == null) {
                GlobalWarming.getInstance().getLogger().warning(String.format("Failed to load config for: [%s]", config.getName()));
                continue;
            }

            if (config.isEnabled()) {
                GlobalWarming.getInstance().getLogger().info(String.format("Loading climate engine for: [%s]", world.getName()));
            } else {
                GlobalWarming.getInstance().getLogger().info(String.format("World: [%s] found, but is disabled", world.getName()));
            }

            //Add the climate engine:
            worldClimateEngines.put(worldId, new WorldClimateEngine(config));

            //Delayed attempt create the world object if it doesn't currently exist:
            WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();
            GWorld gWorld = worldTable.getWorld(worldId);
            if (gWorld == null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        GWorld gw = worldTable.getWorld(worldId);
                        if (gw == null) {
                            worldTable.insertNewWorld(worldId);
                        }
                    }
                }.runTaskLater(GlobalWarming.getInstance(), 40L);
            }
        }
    }

    public WorldClimateEngine getClimateEngine(UUID worldId) {
        WorldClimateEngine climateEngine = null;
        if (worldClimateEngines.containsKey(worldId)) {
            climateEngine = worldClimateEngines.get(worldId);
        }

        if (climateEngine == null) {
            GlobalWarming.getInstance().getLogger().warning(String.format(
                  Lang.ENGINE_NOTFOUND.get(),
                  WorldConfig.getDisplayName(worldId)));
        }

        return climateEngine;
    }

    public boolean isClimateEngineEnabled(UUID worldId) {
        WorldClimateEngine worldClimateEngine = getClimateEngine(worldId);
        return worldClimateEngine != null && worldClimateEngine.isEnabled();
    }

    public static ClimateEngine getInstance() {
        if (climateEngine == null) {
            climateEngine = new ClimateEngine();
        }

        return climateEngine;
    }
}
