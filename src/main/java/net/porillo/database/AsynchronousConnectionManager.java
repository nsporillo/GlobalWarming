package net.porillo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AsynchronousConnectionManager {

	private Connection connection;
	private String host;
	private int port;
	private String database;
	private String username, password;

	public AsynchronousConnectionManager(String host, int port, String database, String username, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public Connection openConnection() throws SQLException, ClassNotFoundException {
		if (connection != null && !connection.isClosed()) {
			return null;
		}

		synchronized (this) {
			if (connection != null && !connection.isClosed()) {
				return null;
			}
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
			return connection;
		}
	}
}
