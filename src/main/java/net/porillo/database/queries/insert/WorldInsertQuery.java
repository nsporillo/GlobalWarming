package net.porillo.database.queries.insert;

import net.porillo.database.api.InsertQuery;
import net.porillo.objects.GWorld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WorldInsertQuery extends InsertQuery {

	private GWorld world;

	public WorldInsertQuery(GWorld world) {
		super("worlds");
		this.world = world;
	}

	public static String getSQL() {
		return "INSERT INTO worlds (worldName, firstSeen, carbonValue, seaLevel, size)" +
				" VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE carbonValue = VALUES(carbonValue), seaLevel = VALUES(seaLevel), size = VALUES(size)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setString(1, world.getWorldName());
		preparedStatement.setLong(2, world.getFirstSeen());
		preparedStatement.setInt(3, world.getCarbonValue());
		preparedStatement.setInt(4, world.getSeaLevel());
		preparedStatement.setInt(5, world.getSize());
		return preparedStatement;
	}
}
