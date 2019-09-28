package net.porillo.database;

import org.h2.Driver;
import org.h2.engine.Engine;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionManager {

    static {
        @SuppressWarnings ("unused") Class<?>[] classes = new Class<?>[] {
                Driver.class,
                Engine.class
        };
    }

    private Connection connection;
    private String host;
    private int port;
    private String database, type;
    private String username, password;

    public ConnectionManager(String type, String host, int port, String database, String username, String password) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            if (type.equalsIgnoreCase("H2")) {
                Class.forName("org.h2.Driver");
                String path = String.format("%s/plugins/GlobalWarming/database", new File(".").getAbsolutePath());
                String jdbcString = "jdbc:h2:file:" + path + ";MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE;IGNORECASE=TRUE";
                return DriverManager.getConnection(jdbcString, username, password);
            } else if (type.equalsIgnoreCase("MYSQL")) {
                Class.forName("com.mysql.jdbc.Driver");
                String connectionString = String.format(
                        "jdbc:mysql://%s:%d/%s?user=%s&password=%s&allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        this.host,
                        this.port,
                        this.database,
                        this.username,
                        this.password);

                connection = DriverManager.getConnection(connectionString);
            }

            return connection;
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
