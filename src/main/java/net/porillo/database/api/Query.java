package net.porillo.database.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Query {

	String getTable();

	String getQueryType();

	String getSQL();

	Long getUniqueID();

	PreparedStatement prepareStatement(Connection connection) throws SQLException;
}
