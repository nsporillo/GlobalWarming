package net.porillo.database.tables;

import net.porillo.GlobalWarming;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.insert.WorldInsertQuery;
import net.porillo.database.queries.select.WorldSelectQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GWorld;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldTable extends Table implements SelectCallback<GWorld> {

	private Map<String, GWorld> worldMap = new HashMap<>();

	public WorldTable() {
		super("worlds");
		createIfNotExists();

		WorldSelectQuery selectQuery = new WorldSelectQuery(this);
		AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
	}

	public GWorld getWorld(String name) {
		if (worldMap.containsKey(name)) {
			return worldMap.get(name);
		}

		return null;
	}

	private void updateWorld(GWorld gWorld) {
		worldMap.put(gWorld.getWorldName(), gWorld);
	}

	public void insertNewWorld(String name) {
		GWorld gWorld = new GWorld();
		gWorld.setUniqueID(GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE));
		gWorld.setWorldName(name);
		gWorld.setFirstSeen(System.currentTimeMillis());
		gWorld.setTemperature(14.0);
		gWorld.setCarbonValue(0);
		gWorld.setSeaLevel(0);
		gWorld.setSize(0);

		updateWorld(gWorld);

		WorldInsertQuery worldInsertQuery = new WorldInsertQuery(gWorld);
		AsyncDBQueue.getInstance().queueInsertQuery(worldInsertQuery);
	}

	@Override
	public void onSelectionCompletion(List<GWorld> returnList) throws SQLException {
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
