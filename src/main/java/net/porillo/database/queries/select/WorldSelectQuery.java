package net.porillo.database.queries.select;

import net.porillo.database.api.SelectQuery;
import net.porillo.database.tables.WorldTable;
import net.porillo.objects.GWorld;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorldSelectQuery extends SelectQuery<GWorld, WorldTable> {

	public WorldSelectQuery(WorldTable callback) {
		super("worlds", callback);
	}

	@Override
	public List<GWorld> queryDatabase(Connection connection) throws SQLException {
		List<GWorld> worlds = new ArrayList<>();
		ResultSet rs = prepareStatement(connection).executeQuery(getSQL());

		while (rs.next()) {
			worlds.add(new GWorld(rs));
		}

		return worlds;
	}

	@Override
	public String getSQL() {
		return "SELECT * FROM worlds";
	}
}
