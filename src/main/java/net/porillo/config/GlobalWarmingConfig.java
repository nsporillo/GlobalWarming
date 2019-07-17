package net.porillo.config;

import lombok.Getter;
import net.porillo.database.ConnectionManager;
import net.porillo.database.api.DbType;

public class GlobalWarmingConfig extends ConfigLoader {

	@Getter private DbType dbType;
	private String host;
	private int port;
	private String database, path;
	private String username, password;
	@Getter private int maxBounties;
	@Getter private int chatTableWidth;
	@Getter private double degreesUntilChangeDetected;
	@Getter private int spamInterval;
	@Getter private int databaseInterval;
	@Getter private int notificationInterval;
	@Getter private int notificationDuration;
	@Getter private boolean scoreboardEnabled;
	@Getter private int scoreboardInterval;
	@Getter private String temperatureFormat;

	public GlobalWarmingConfig() {
		super("config.yml");
		super.saveIfNotExist();
		super.load();
	}

	@Override
	protected void loadKeys() {
		this.chatTableWidth = conf.getInt("chat.table-width", 280);
		this.maxBounties = conf.getInt("bounty.max-created-per-player", 5);
		this.degreesUntilChangeDetected = conf.getDouble("climate-notification.degrees-until-change-detected", 0.25);
		this.spamInterval = conf.getInt("commands.spam-interval", 60);

		this.dbType = DbType.valueOf(conf.getString("database.type", "SQLITE").toUpperCase());
		this.path = conf.getString("database.path", "plugins/GlobalWarming/gw.db");
		this.host = conf.getString("database.host");
		this.port = conf.getInt("database.port");
		this.database = conf.getString("database.name");
		this.username = conf.getString("database.username");
		this.password = conf.getString("database.password");
		this.databaseInterval = conf.getInt("database.interval", 300);

		this.notificationInterval = conf.getInt("notification.interval", 6000);
		this.notificationDuration = conf.getInt("notification.duration", 300);

		this.scoreboardEnabled = conf.getBoolean("scoreboard.enabled", true);
		this.scoreboardInterval = conf.getInt("scoreboard.interval", 20);
		this.temperatureFormat = conf.getString("temperature.format", "#.##");
	}

	public ConnectionManager makeConnectionManager() {
		return new ConnectionManager(dbType, host, port, database, path, username, password);
	}

} 
