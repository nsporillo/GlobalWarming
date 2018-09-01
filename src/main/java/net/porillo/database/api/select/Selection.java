package net.porillo.database.api.select;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
public class Selection {

	private String tableName;
	private String sql;

	public ResultSet makeSelection(Connection connection) throws SQLException {
		return connection.createStatement().executeQuery(sql);
	}
}
