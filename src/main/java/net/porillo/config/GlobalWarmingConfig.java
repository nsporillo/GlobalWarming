package net.porillo.config;

import net.porillo.database.AsynchronousConnectionManager;
import net.porillo.engine.ClimateEngine;
import net.porillo.objects.World;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;

public class GlobalWarmingConfig extends ConfigLoader {

	private String host;
	private int port;
	private String database;
	private String username, password;
	private Map<World, ClimateEngine> engineMap;

	public GlobalWarmingConfig(Plugin plugin) {
		super(plugin, "config.yml");
		super.saveIfNotExist();
		super.load();
		this.engineMap = new HashMap<>();
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
	
	public ClimateEngine getEngine(World world) {
		if (engineMap.containsKey(world)) {
			return engineMap.get(world);
		} else {
			engineMap.put(world, new ClimateEngine(world));
			return getEngine(world);
		}
	}
} 
