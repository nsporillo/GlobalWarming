package net.porillo.database.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public interface Query {

    String getTable();

    String getSQL();

    Statement prepareStatement(Connection connection) throws SQLException;
}
