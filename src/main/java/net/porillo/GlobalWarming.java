package net.porillo;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageKeys;
import co.aikar.locales.MessageKeyProvider;
import com.google.common.collect.ImmutableList;
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
import net.porillo.engine.api.Model;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.listeners.AttributionListener;
import net.porillo.listeners.CO2Listener;
import net.porillo.listeners.PlayerListener;
import net.porillo.listeners.WorldListener;
import net.porillo.objects.GPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class GlobalWarming extends JavaPlugin {

	private static GlobalWarming instance; // single plugin instance

	@Getter private GlobalWarmingConfig conf;
	@Getter private ConnectionManager connectionManager;
	@Getter private TableManager tableManager;
	@Getter private Random random;
	private BukkitCommandManager commandManager;
	@Getter private Gson gson;

	@Override
	public void onEnable() {
		instance = this;

		Lang.init();
		this.random = new Random();
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.conf = new GlobalWarmingConfig();
		this.connectionManager = conf.makeConnectionManager();
		this.tableManager = new TableManager();
		// Load Engines
		ClimateEngine.getInstance().loadWorldClimateEngines();
		EffectEngine.getInstance();
		// Load Commands
		this.commandManager = new BukkitCommandManager(this);
		registerCommands();

		// Load Listeners
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

		commandManager.getCommandCompletions().registerCompletion("model", c -> {
			WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(c.getPlayer().getWorld().getName());
			Set<String> names = new HashSet<>();
			for (Model model : climateEngine.getModels().values()) {
				names.add(model.getModelName().replace(".json", ""));
			}

			return names;
		});
		commandManager.getCommandCompletions().registerCompletion("config", c -> ImmutableList.of("lang", "config"));

		commandManager.registerCommand(new AdminCommands());
		commandManager.registerCommand(new GeneralCommands());
	}

	/**
	 * @return instance of main class
	 */
	public static GlobalWarming getInstance() {
		return instance;
	}
}
