package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.queries.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTable extends Table {

	@Getter
	private Map<UUID, GPlayer> players = new HashMap<>();

	public PlayerTable() {
		super("players");
		createIfNotExists();
	}

	@Override
	public void createIfNotExists() {
		String sql = "CREATE TABLE IF NOT EXISTS players (\n" +
				"  uuid VARCHAR(36) NOT NULL,\n" +
				"  firstSeen LONG,\n" +
				"  carbonScore INT,\n" +
				"  PRIMARY KEY (uuid)\n" +
				")";
		CreateTableQuery createTableQuery = new CreateTableQuery("players", sql);
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
	}

	public List<GPlayer> loadTable() {
		return null;
	}
}
