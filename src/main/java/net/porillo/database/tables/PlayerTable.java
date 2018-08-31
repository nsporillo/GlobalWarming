package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.queries.insert.PlayerInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTable extends Table {

	@Getter private Map<UUID, GPlayer> players = new HashMap<>();

	public PlayerTable() {
		super("players");
		createIfNotExists();
	}


	public List<GPlayer> loadTable() {
		return null;
	}

	public GPlayer getOrCreatePlayer(UUID uuid, boolean untracked) {
		if (players.containsKey(uuid)) {
			return players.get(uuid);
		} else {
			// Create new player object
			Long uniqueId = untracked ? 0L : GlobalWarming.getInstance().getRandom().nextLong();
			GPlayer gPlayer = new GPlayer(uniqueId, uuid, System.currentTimeMillis(), 0);

			// Store player object
			players.put(uuid, gPlayer);

			// Queue a player insert
			PlayerInsertQuery insertQuery = new PlayerInsertQuery(gPlayer);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

			return gPlayer;
		}
	}
}
