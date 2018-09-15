package net.porillo.config;

import net.porillo.database.ConnectionManager;

public class GlobalWarmingConfig extends ConfigLoader {

	private String host;
	private int port;
	private String database;
	private String username, password;
	public int updateInterval;

	public GlobalWarmingConfig() {
		super("config.yml");
		super.saveIfNotExist();
		super.load();
	}

	@Override
	protected void loadKeys() {
		this.host = conf.getString("database.host");
		this.port = conf.getInt("database.port");
		this.database = conf.getString("database.name");
		this.username = conf.getString("database.username");
		this.password = conf.getString("database.password");
		this.updateInterval = conf.getInt("database.update-interval", 300);
	}

	public ConnectionManager makeConnectionManager() {
		return new ConnectionManager(host, port, database, username, password);
	}

} 
