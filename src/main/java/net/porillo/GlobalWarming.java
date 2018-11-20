package net.porillo;

import co.aikar.commands.BukkitCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.porillo.commands.AdminCommands;
import net.porillo.commands.GeneralCommands;
import net.porillo.config.GlobalWarmingConfig;
import net.porillo.config.Lang;
import net.porillo.database.ConnectionManager;
import net.porillo.database.TableManager;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.effect.EffectEngine;
import net.porillo.engine.ClimateEngine;
import net.porillo.listeners.AttributionListener;
import net.porillo.listeners.CO2Listener;
import net.porillo.listeners.PlayerListener;
import net.porillo.listeners.WorldListener;
import net.porillo.objects.GPlayer;
import net.porillo.util.CO2Notifications;
import net.porillo.util.GScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.*;

public class GlobalWarming extends JavaPlugin {

	private static GlobalWarming instance; // single plugin instance

	@Getter private GlobalWarmingConfig conf;
	@Getter private ConnectionManager connectionManager;
	@Getter private TableManager tableManager;
	@Getter private Random random;
	private BukkitCommandManager commandManager;
	@Getter private Gson gson;
    @Getter	private GScoreboard scoreboard;
    @Getter	private CO2Notifications notifications;

	@Override
	public void onEnable() {
		instance = this;

		Lang.init();
		this.random = new Random();
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.conf = new GlobalWarmingConfig();
		this.connectionManager = conf.makeConnectionManager();
		this.tableManager = new TableManager();

		try {
			//Load all the table data immediately:
			Connection connection = GlobalWarming.getInstance().getConnectionManager().openConnection();
			AsyncDBQueue.getInstance().writeSelectQueue(connection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ClimateEngine.getInstance().loadWorldClimateEngines();
		EffectEngine.getInstance();
		this.commandManager = new BukkitCommandManager(this);
		this.scoreboard = new GScoreboard();
		this.notifications = new CO2Notifications();
		registerCommands();

		Bukkit.getPluginManager().registerEvents(new AttributionListener(this), this);
		Bukkit.getPluginManager().registerEvents(new CO2Listener(this), this);
		Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

		AsyncDBQueue.getInstance().scheduleAsyncTask(conf.getDatabaseInterval() * 20L);
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
