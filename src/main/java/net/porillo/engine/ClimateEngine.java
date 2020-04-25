package net.porillo.engine;

import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.config.WorldConfig;
import net.porillo.database.tables.WorldTable;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClimateEngine {

    private static ClimateEngine climateEngine;
    private Map<UUID, WorldClimateEngine> worldClimateEngines;

    public ClimateEngine() {
        this.worldClimateEngines = new HashMap<>();
    }

    public void loadWorldClimateEngine(World world) {
        if (world == null) return;
        UUID worldId = world.getUID();
        if (!worldClimateEngines.containsKey(worldId)) {
            WorldConfig worldConfig = new WorldConfig(worldId);

            if (!worldConfig.isEnabled()) {
                GlobalWarming.getInstance().getLogger().info(String.format("World: [%s] found, but is disabled", world.getName()));
            } else {
                GlobalWarming.getInstance().getLogger().info(String.format("Loading climate engine for: [%s]", world.getName()));
            }

            //Add the climate engine:
            worldClimateEngines.put(worldId, new WorldClimateEngine(worldConfig));

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

    public void loadWorldClimateEngines() {
        for (World world : Bukkit.getWorlds()) {
            loadWorldClimateEngine(world);
        }
    }

    public WorldClimateEngine getClimateEngine(UUID worldId) {
        WorldClimateEngine climateEngine = worldClimateEngines.get(worldId);

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
