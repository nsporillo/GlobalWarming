package net.porillo.config;

import lombok.Getter;
import net.porillo.database.ConnectionManager;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class GlobalWarmingConfig extends ConfigLoader {

	private String host;
	private int port;
	private String database;
	private String username, password;
	public int updateInterval;
    @Getter private List<String> enabledWorlds;

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
		this.updateInterval = conf.getInt("database.update-interval", 300);
		this.enabledWorlds = conf.getStringList("worlds");
	}

	public ConnectionManager makeConnectionManager() {
		return new ConnectionManager(host, port, database, username, password);
	}

	@Override
	protected void reload() {
		// clear memory
		super.rereadFromDisk();
		super.load();
	}
} 
