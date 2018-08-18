package net.porillo.config;

import net.porillo.database.AsynchronousConnectionManager;
import org.bukkit.plugin.Plugin;

public class GlobalWarmingConfig extends ConfigLoader {

	private String host;
	private int port;
	private String database;
	private String username, password;

	public GlobalWarmingConfig(Plugin plugin) {
		super(plugin, "config.yml");
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
	}

	public AsynchronousConnectionManager makeConnectionManager() {
		return new AsynchronousConnectionManager(host, port, database, username, password);
	}

	@Override
	protected void reload() {
		// clear memory
		super.rereadFromDisk();
		super.load();
	}
}
