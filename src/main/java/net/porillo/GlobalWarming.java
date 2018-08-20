package net.porillo;

import lombok.Getter;
import net.porillo.config.GlobalWarmingConfig;
import net.porillo.database.AsynchronousConnectionManager;
import net.porillo.database.TableManager;
import net.porillo.engine.ClimateEngine;
import net.porillo.listeners.AttributionListener;
import net.porillo.listeners.CO2Listener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GlobalWarming extends JavaPlugin {

	@Getter private GlobalWarmingConfig conf;
	@Getter private AsynchronousConnectionManager connectionManager;
	@Getter private TableManager tableManager;

	@Override
	public void onEnable() {
		this.conf = new GlobalWarmingConfig(this);
		this.connectionManager = conf.makeConnectionManager();
		this.tableManager = new TableManager();

		Bukkit.getPluginManager().registerEvents(new AttributionListener(this), this);
		Bukkit.getPluginManager().registerEvents(new CO2Listener(this), this);


	}

	@Override
	public void onDisable() {

	}

}
