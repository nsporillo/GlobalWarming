package net.porillo.config;

import lombok.Getter;
import net.porillo.database.ConnectionManager;

public class GlobalWarmingConfig extends ConfigLoader {

	private String host;
	private int port;
	private String database;
	private String username, password;
	@Getter private int maxBounties;
	@Getter private double defaultIceLevel;
	@Getter private double defaultSnowLevel;
	@Getter private double defaultFarmYieldFitness;
	@Getter private double defaultMobFitness;
	@Getter private double defaultTemperature;
	@Getter private double lowTemperatureUBound;
	@Getter private double highTemperatureLBound;
	@Getter private int spamInterval;
	@Getter private int databaseInterval;
	@Getter private int notificationInterval;
	@Getter private int notificationDuration;
	@Getter private int defaultSeaLevel;
	@Getter private int scoreboardInterval;

	public GlobalWarmingConfig() {
		super("config.yml");
		super.saveIfNotExist();
		super.load();
	}

	@Override
	protected void loadKeys() {
		this.maxBounties = conf.getInt("bounty.max-created-per-player", 5);

		this.defaultSeaLevel = conf.getInt("climate.default-sea-level", 63);
		this.defaultIceLevel = conf.getDouble("climate.default-ice-level", 0.0);
		this.defaultSnowLevel = conf.getDouble("climate.default-snow-level", 0.0);
		this.defaultFarmYieldFitness = conf.getDouble("climate.default-farm-yield-fitness", 100.0);
		this.defaultMobFitness = conf.getDouble("climate.default-mob-fitness", 100.0);
		this.defaultTemperature = conf.getDouble("climate.default-temperature", 14.0);
		this.lowTemperatureUBound = conf.getDouble("climate.low-temperature-ubound", 13.75);
		this.highTemperatureLBound = conf.getDouble("climate.high-temperature-lbound", 14.25);

		this.spamInterval = conf.getInt("commands.spam-interval", 60);

		this.host = conf.getString("database.host");
		this.port = conf.getInt("database.port");
		this.database = conf.getString("database.name");
		this.username = conf.getString("database.username");
		this.password = conf.getString("database.password");
		this.databaseInterval = conf.getInt("database.interval", 300);

		this.notificationInterval = conf.getInt("notification.interval", 6000);
		this.notificationDuration = conf.getInt("notification.duration", 300);

		this.scoreboardInterval = conf.getInt("scoreboard.interval", 20);
	}

	public ConnectionManager makeConnectionManager() {
		return new ConnectionManager(host, port, database, username, password);
	}

} 
