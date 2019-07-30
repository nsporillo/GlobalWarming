package net.porillo.config;

import lombok.Getter;
import net.porillo.database.ConnectionManager;

@Getter
public class GlobalWarmingConfig extends ConfigLoader {

    private String host;
    private int port;
    private String database;
    private String username, password;
    private int maxBounties;
    private int chatTableWidth;
    private double degreesUntilChangeDetected;
    private int spamInterval;
    private int databaseInterval;
    private int notificationInterval;
    private int notificationDuration;
    private boolean scoreboardEnabled;
    private int scoreboardInterval;
    private String temperatureFormat;

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

        this.scoreboardEnabled = conf.getBoolean("scoreboard.enabled", true);
        this.scoreboardInterval = conf.getInt("scoreboard.interval", 20);
        this.temperatureFormat = conf.getString("temperature.format", "#.##");
    }

    public ConnectionManager makeConnectionManager() {
        return new ConnectionManager(host, port, database, username, password);
    }

} 
