package net.porillo.database.tables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.select.Selection;
import net.porillo.database.api.select.SelectionListener;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@AllArgsConstructor
public abstract class Table implements SelectionListener {

	@Getter private String tableName;

	public void createIfNotExists() {
		CreateTableQuery createTableQuery = new CreateTableQuery(getTableName(), loadSQLFromFile());
		AsyncDBQueue.getInstance().queueCreateQuery(createTableQuery);
	}

	public abstract Selection makeSelectionQuery();

	public Path getPath() {
		if (GlobalWarming.getInstance() != null) {
			return GlobalWarming.getInstance().getDataFolder().toPath().resolve("scripts").resolve(tableName + ".sql");
		}
		// For testing only. Plugin instance should never be null.
		return Paths.get("src/test/resources/scripts").resolve(tableName + ".sql");
	}

	private void copyFromResource() {
		Path file = getPath();

		if (!Files.exists(file)) {
			GlobalWarming.getInstance().getLogger().info("Script " + tableName + ".sql" + " does not exist, creating.");
			GlobalWarming.getInstance().saveResource("scripts/" + tableName + ".sql", false);
		}
	}

	public String loadSQLFromFile() {
		this.copyFromResource();
		StringBuilder builder = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(getPath())))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("--")) {
					builder.append(line);
				}
			}
		} catch (NullPointerException | IOException ex) {
			ex.printStackTrace();
		}

		return builder.toString();
	}
}
