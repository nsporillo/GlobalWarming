package net.porillo;

import lombok.Getter;
import net.porillo.commands.CommandHandler;
import net.porillo.config.GlobalWarmingConfig;
import net.porillo.database.ConnectionManager;
import net.porillo.database.TableManager;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.engine.ClimateEngine;
import net.porillo.listeners.AttributionListener;
import net.porillo.listeners.CO2Listener;
import net.porillo.listeners.PlayerListener;
import net.porillo.listeners.WorldListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GlobalWarming extends JavaPlugin {

	private static GlobalWarming instance; // single plugin instance

	@Getter private GlobalWarmingConfig conf;
	@Getter private ConnectionManager connectionManager;
	@Getter private TableManager tableManager;
	private CommandHandler commandHandler;
	private ClimateEngine climateEngine;

	@Override
	public void onEnable() {
		instance = this;

		this.conf = new GlobalWarmingConfig(this);
		this.connectionManager = conf.makeConnectionManager();
		this.tableManager = new TableManager();
		this.commandHandler = new CommandHandler(this);
		this.climateEngine = new ClimateEngine();

		Bukkit.getPluginManager().registerEvents(new AttributionListener(this), this);
		Bukkit.getPluginManager().registerEvents(new CO2Listener(this), this);
		Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

		AsyncDBQueue.getInstance().scheduleAsyncTask(conf.updateInterval * 20L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		commandHandler.runCommand(sender, label, args);
		return true;
	}

	@Override
	public void onDisable() {

	}

	/**
	 * @return instance of main class
	 */
	public static GlobalWarming getInstance() {
		return instance;
	}
}
