package net.porillo.database.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Query {

	public String getTable();

	public String getQueryType();

	public String getSQL();

	public PreparedStatement prepareStatement(Connection connection) throws SQLException;
}
