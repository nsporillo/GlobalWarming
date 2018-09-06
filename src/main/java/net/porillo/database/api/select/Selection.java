package net.porillo.database.api.select;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Selection {

	private String tableName;
	private String sql;
	private UUID uuid;

	public Selection(String tableName, String sql) {
		this.tableName = tableName;
		this.sql = sql;
		this.uuid = UUID.randomUUID();
	}

	public ResultSet makeSelection(Connection connection) throws SQLException {
		return connection.createStatement().executeQuery(sql);
	}
}
