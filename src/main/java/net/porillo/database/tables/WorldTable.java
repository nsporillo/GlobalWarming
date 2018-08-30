package net.porillo.database.tables;

import net.porillo.GlobalWarming;
import net.porillo.database.queries.insert.WorldInsertQuery;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GWorld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldTable extends Table {

	private Map<String, GWorld> worldMap = new HashMap<>();

	public WorldTable() {
		super("worlds");
		createIfNotExists();
	}

	@Override
	public void createIfNotExists() {
		String sql = "CREATE TABLE IF NOT EXISTS worlds (\n" +
				"  worldName VARCHAR(36) NOT NULL,\n" +
				"  firstSeen LONG,\n" +
				"  carbonValue INT,\n" +
				"  seaLevel INT,\n" +
				"  size INT,\n" +
				"  PRIMARY KEY (worldName)\n" +
				");";
		CreateTableQuery createTableQuery = new CreateTableQuery(getTableName(), sql);
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
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
		gWorld.setWorldName(name);
		gWorld.setFirstSeen(System.currentTimeMillis());
		gWorld.setTemperature(14.0);
		gWorld.setCarbonValue(0);
		gWorld.setSize(0);

		updateWorld(gWorld);

		WorldInsertQuery worldInsertQuery = new WorldInsertQuery(gWorld);

		try {
			Connection connection = GlobalWarming.getInstance().getConnectionManager().openConnection();
			PreparedStatement preparedStatement = worldInsertQuery.prepareStatement(connection);
			preparedStatement.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return gWorld;
	}

	public List<GWorld> loadTable() {
		return null;
	}
}
