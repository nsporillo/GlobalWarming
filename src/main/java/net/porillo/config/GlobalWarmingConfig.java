package net.porillo.config;

import lombok.Getter;
import net.porillo.database.ConnectionManager;

public class GlobalWarmingConfig extends ConfigLoader {

	private String host;
	private int port;
	private String database;
	private String username, password;
	@Getter private int maxBounties;
	@Getter private int chatTableWidth;
	@Getter private double degreesUntilChangeDetected;
	@Getter private int spamInterval;
	@Getter private int databaseInterval;
	@Getter private int notificationInterval;
	@Getter private int notificationDuration;
	@Getter private int scoreboardInterval;
	@Getter private int seaLevelChunksPerPeriod;
	@Getter private int seaLevelQueueTicks;
	@Getter private int seaLevelChunkTicks;
	@Getter private int weatherCheckInterval;

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

		this.host = conf.getString("database.host");
		this.port = conf.getInt("database.port");
		this.database = conf.getString("database.name");
		this.username = conf.getString("database.username");
		this.password = conf.getString("database.password");
		this.databaseInterval = conf.getInt("database.interval", 300);

		this.notificationInterval = conf.getInt("notification.interval", 6000);
		this.notificationDuration = conf.getInt("notification.duration", 300);

		this.scoreboardInterval = conf.getInt("scoreboard.interval", 20);

		this.seaLevelChunksPerPeriod = conf.getInt("sea-level.chunks-per-period", 6);
		this.seaLevelQueueTicks = conf.getInt("sea-level.queue-ticks", 60);
		this.seaLevelChunkTicks = conf.getInt("sea-level.chunk-ticks", 30);

		this.weatherCheckInterval = conf.getInt("weather.interval", 3600);
	}

	public ConnectionManager makeConnectionManager() {
		return new ConnectionManager(host, port, database, username, password);
	}

} 
