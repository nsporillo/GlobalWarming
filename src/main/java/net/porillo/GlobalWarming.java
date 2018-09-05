package net.porillo;

import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import net.porillo.commands.AdminCommands;
import net.porillo.commands.GeneralCommands;
import net.porillo.config.GlobalWarmingConfig;
import net.porillo.database.ConnectionManager;
import net.porillo.database.TableManager;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.engine.ClimateEngine;
import net.porillo.listeners.AttributionListener;
import net.porillo.listeners.CO2Listener;
import net.porillo.listeners.PlayerListener;
import net.porillo.listeners.WorldListener;
import net.porillo.objects.GPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class GlobalWarming extends JavaPlugin {

	private static GlobalWarming instance; // single plugin instance

	@Getter private GlobalWarmingConfig conf;
	@Getter private ConnectionManager connectionManager;
	@Getter private TableManager tableManager;
	@Getter private Random random;
	private BukkitCommandManager commandManager;

	@Override
	public void onEnable() {
		instance = this;

		this.random = new Random();
		this.conf = new GlobalWarmingConfig(this);
		this.connectionManager = conf.makeConnectionManager();
		this.tableManager = new TableManager();
		ClimateEngine.getInstance().loadWorldClimateEngines(this.conf.getEnabledWorlds());
		this.commandManager = new BukkitCommandManager(this);
		registerCommands();

		Bukkit.getPluginManager().registerEvents(new AttributionListener(this), this);
		Bukkit.getPluginManager().registerEvents(new CO2Listener(this), this);
		Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

		AsyncDBQueue.getInstance().scheduleAsyncTask(conf.updateInterval * 20L);
	}

	@Override
	public void onDisable() {
		AsyncDBQueue.getInstance().close();
	}

	private void registerCommands() {
		commandManager.enableUnstableAPI("help");
		commandManager.getCommandContexts().registerIssuerOnlyContext(GPlayer.class, c -> tableManager.getPlayerTable().getPlayers().get(c.getPlayer().getUniqueId()));

		this.commandManager.registerCommand(new AdminCommands());
		this.commandManager.registerCommand(new GeneralCommands());
	}

	/**
	 * @return instance of main class
	 */
	public static GlobalWarming getInstance() {
		return instance;
	}
}
