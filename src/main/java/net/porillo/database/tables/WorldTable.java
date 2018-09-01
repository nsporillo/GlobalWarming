package net.porillo.database.tables;

import net.porillo.GlobalWarming;
import net.porillo.database.api.select.Selection;
import net.porillo.database.api.select.SelectionResult;
import net.porillo.database.queries.insert.WorldInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GWorld;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldTable extends Table {

	private Map<String, GWorld> worldMap = new HashMap<>();

	public WorldTable() {
		super("worlds");
		createIfNotExists();
		AsyncDBQueue.getInstance().queueSelection(makeSelectionQuery(), this);
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
		gWorld.setUniqueID(GlobalWarming.getInstance().getRandom().nextInt());
		gWorld.setWorldName(name);
		gWorld.setFirstSeen(System.currentTimeMillis());
		gWorld.setTemperature(14.0);
		gWorld.setCarbonValue(0);
		gWorld.setSeaLevel(0);
		gWorld.setSize(0);

		updateWorld(gWorld);

		WorldInsertQuery worldInsertQuery = new WorldInsertQuery(gWorld);
		AsyncDBQueue.getInstance().queueInsertQuery(worldInsertQuery);

		return gWorld;
	}

	@Override
	public Selection makeSelectionQuery() {
		String sql = "SELECT * FROM worlds;";
		return new Selection(getTableName(), sql);
	}

	@Override
	public void onResultArrival(SelectionResult result) throws SQLException {
		if (result.getTableName().equals(getTableName())) {
			List<GWorld> gWorlds = new ArrayList<>();
			ResultSet rs = result.getResultSet();

			while (rs.next()) {
				gWorlds.add(new GWorld(rs));
			}

			new BukkitRunnable() {

				@Override
				public void run() {
					for (GWorld world : gWorlds) {
						updateWorld(world);
					}
				}
			}.runTask(GlobalWarming.getInstance());
		}
	}
}
