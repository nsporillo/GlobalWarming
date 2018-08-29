package net.porillo.database.queries.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.porillo.database.api.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor
public class CreateTableQuery implements Query {

	@Getter private String table;
	private String sql;

	@Override
	public String getQueryType() {
		return "create";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		return connection.prepareStatement(this.sql);
	}
}
