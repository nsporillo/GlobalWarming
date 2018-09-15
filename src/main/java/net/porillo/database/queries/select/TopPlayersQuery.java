package net.porillo.database.queries.select;

import net.porillo.database.api.SelectCallback;
import net.porillo.database.api.SelectQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopPlayersQuery extends SelectQuery<Object[], SelectCallback<Object[]>> {

	public TopPlayersQuery(String table, SelectCallback<Object[]> callback) {
		super(table, callback);
	}

	@Override
	public List<Object[]> queryDatabase(Connection connection) throws SQLException {
		List<Object[]> topPlayers = new ArrayList<>();
		ResultSet rs = prepareStatement(connection).executeQuery(getSQL());

		while (rs.next()) {
			Object[] result = new Object[2];
			result[0] = rs.getString(1);
			result[1] = rs.getInt(2);
			topPlayers.add(result);
		}

		return topPlayers;
	}

	@Override
	public String getSQL() {
		return "SELECT uuid,carbonScore FROM players ORDER BY carbonScore ASC LIMIT 10";
	}

}
