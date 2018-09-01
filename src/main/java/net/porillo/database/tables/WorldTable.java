package net.porillo.database.tables;

import net.porillo.GlobalWarming;
import net.porillo.database.queries.insert.WorldInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GWorld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldTable extends Table {

	private Map<String, GWorld> worldMap = new HashMap<>();

	public WorldTable() {
		super("worlds");
		createIfNotExists();
	}

	public GWorld getWorld(String name) {
		if (worldMap.containsKey(name)) {
			return worldMap.get(name);
		}

		return null;
	}

	public void updateWorld(GWorld gWorld) {
		worldMap.put(gWorld.getWorldName(), gWorld);
	}

	public GWorld insertNewWorld(String name) {
		GWorld gWorld = new GWorld();
		gWorld.setUniqueID(GlobalWarming.getInstance().getRandom().nextLong());
		gWorld.setWorldName(name);
		gWorld.setFirstSeen(System.currentTimeMillis());
		gWorld.setTemperature(14.0);
		gWorld.setCarbonValue(0);
		gWorld.setSize(0);
		gWorld.setUniqueID(GlobalWarming.getInstance().getRandom().nextLong());

		updateWorld(gWorld);

		WorldInsertQuery worldInsertQuery = new WorldInsertQuery(gWorld);
		AsyncDBQueue.getInstance().queueInsertQuery(worldInsertQuery);

		return gWorld;
	}

	public List<GWorld> loadTable() {
		return null;
	}
}
