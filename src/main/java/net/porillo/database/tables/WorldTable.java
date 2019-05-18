package net.porillo.database.tables;

import net.porillo.GlobalWarming;
import net.porillo.config.WorldConfig;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.insert.WorldInsertQuery;
import net.porillo.database.queries.select.WorldSelectQuery;
import net.porillo.database.queries.update.WorldUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GWorld;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldTable extends Table implements SelectCallback<GWorld> {

    private Map<UUID, GWorld> worldMap = new HashMap<>();

    public WorldTable() {
        super("worlds");
        createIfNotExists();

        WorldSelectQuery selectQuery = new WorldSelectQuery(this);
        AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
    }

    public GWorld getWorld(UUID worldId) {
        if (worldMap.containsKey(worldId)) {
            return worldMap.get(worldId);
        }

        return insertNewWorld(worldId);
    }

    private void updateWorld(GWorld gWorld) {
        worldMap.put(gWorld.getWorldId(), gWorld);
    }

    public GWorld insertNewWorld(UUID worldId) {
        GWorld gWorld = new GWorld();
        gWorld.setUniqueID(GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE));
        gWorld.setWorldId(worldId);
        gWorld.setFirstSeen(System.currentTimeMillis());
        gWorld.setCarbonValue(0);
        gWorld.setSeaLevel(0);
        gWorld.setSize(0);

        updateWorld(gWorld);

        WorldInsertQuery worldInsertQuery = new WorldInsertQuery(gWorld);
        AsyncDBQueue.getInstance().queueInsertQuery(worldInsertQuery);
        GlobalWarming.getInstance().getLogger().info(String.format(
              "Record created for world: [%s]",
              WorldConfig.getDisplayName(worldId)));
        return gWorld;
    }

    public void updateWorldCarbonValue(UUID worldId, int value) {
        GWorld affectedWorld = this.getWorld(worldId);

        if (affectedWorld != null) {
            int carbon = affectedWorld.getCarbonValue();
            affectedWorld.setCarbonValue(carbon + value);

            //Queue an update to the world table:
            WorldUpdateQuery worldUpdateQuery = new WorldUpdateQuery(affectedWorld);
            AsyncDBQueue.getInstance().queueUpdateQuery(worldUpdateQuery);
        }
    }

    @Override
    public void onSelectionCompletion(List<GWorld> returnList) {
        if (GlobalWarming.getInstance() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (GWorld world : returnList) {
                        updateWorld(world);
                    }
                }
            }.runTask(GlobalWarming.getInstance());
        } else {
            System.out.printf("Selection returned %d worlds.%n", returnList.size());
        }
    }
}
