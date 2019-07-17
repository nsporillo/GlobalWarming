package net.porillo.database;

import net.porillo.database.api.DbType;
import net.porillo.util.Updater;
import org.bukkit.Bukkit;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

public class ConnectionManager {

	private DbType type;
	private Connection connection;
	private String host;
	private int port;
	private String database, path;
	private String username, password;

	public ConnectionManager(DbType type, String host, int port, String database, String path, String username, String password) {
		this.type = type;
		this.host = host;
		this.port = port;
		this.database = database;
		this.path = path;
		this.username = username;
		this.password = password;
	}

	public Connection openConnection() throws Exception {
		if (connection != null && !connection.isClosed()) {
			return connection;
		}

		synchronized (this) {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}

			ClassLoader classLoader;

			if (type == DbType.SQLITE) {
				classLoader = new URLClassLoader(new URL[]{new URL("jar:file:" + new File(Updater.LIBDIR + "sqlite.jar").getPath() + "!/")});
			} else {
				classLoader = Bukkit.getServer().getClass().getClassLoader();
			}

			String className = "";
			if (type == DbType.MYSQL) {
				className = "com.mysql.jdbc.Driver";
			} else {
				className = "org.sqlite.JDBC";
			}

			Driver driver = (Driver) classLoader.loadClass(className).newInstance();

			Properties properties = new Properties();
			properties.put("autoReconnect", "true");
			properties.put("useSSL", "false");
			properties.put("useUnicode", "true");
			properties.put("user", this.username);
			properties.put("password", this.password);

			String connect = String.format("jdbc:%s:%s", type.toString().toLowerCase(), getDatabasePath());
			connection = driver.connect(connect, properties);
			return connection;
		}
	}

	private String getDatabasePath() {
		return type == DbType.MYSQL ? "//" + this.host + ":" + this.port + "/" + this.database : this.path;
	}
}
