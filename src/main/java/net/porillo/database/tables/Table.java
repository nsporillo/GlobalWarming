package net.porillo.database.tables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@AllArgsConstructor
public abstract class Table {

    @Getter private String tableName;

    public void createIfNotExists() {
        CreateTableQuery createTableQuery = new CreateTableQuery(getTableName(), loadSQLFromFile());
        AsyncDBQueue.getInstance().queueCreateQuery(createTableQuery);
    }

    public Path getPath() {
        if (GlobalWarming.getInstance() != null) {
            Path path = GlobalWarming.getInstance().getDataFolder().toPath().resolve("scripts").resolve(String.format("%s.sql", tableName));
            if (!Files.exists(path)) {
                GlobalWarming.getInstance().saveResource(String.format("scripts/%s.sql", tableName), false);
            }
            return GlobalWarming.getInstance().getDataFolder().toPath().resolve("scripts").resolve(String.format("%s.sql", tableName));
        } else {
            try {
                return Paths.get(getClass().getResource("/scripts").toURI()).resolve(String.format("%s.sql", tableName));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void copyFromResource() {
        Path path = getPath();
        if (path == null || !Files.exists(path)) {
            GlobalWarming.getInstance().getLogger().info(String.format(
                    "Script: [%s.sql] does not exist at: [%s], creating",
                    tableName,
                    path));

            GlobalWarming.getInstance().saveResource(String.format("scripts/%s.sql", tableName), false);
        }
    }

    public String loadSQLFromFile() {
        this.copyFromResource();
        StringBuilder builder = new StringBuilder();
        Path path = getPath();
        if (path != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("--")) {
                        builder.append(line);
                    }
                }
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }

        return builder.toString();
    }
}
