package net.porillo.database.queries;

import lombok.AllArgsConstructor;
import net.porillo.database.api.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor
public class CreateTableQuery implements Query {

	private String table;
	private String sql;

	@Override
	public String getTable() {
		return table;
	}

	@Override
	public String getQueryType() {
		return "create";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		return connection.prepareStatement(this.sql);
	}
}
