package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.insert.PlayerInsertQuery;
import net.porillo.database.queries.select.PlayerSelectQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTable extends Table implements SelectCallback<GPlayer> {

	@Getter private Map<UUID, GPlayer> players = new HashMap<>();
	@Getter private Map<Integer, UUID> uuidMap = new HashMap<>();

	public PlayerTable() {
		super("players");
		createIfNotExists();

		PlayerSelectQuery selectQuery = new PlayerSelectQuery(this);
		AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
	}

	public GPlayer getOrCreatePlayer(UUID uuid, boolean untracked) {
		if (players.containsKey(uuid)) {
			return players.get(uuid);
		} else {
			// Create new player object
			Integer uniqueId = untracked ? 0 : GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
			GPlayer gPlayer = new GPlayer(uniqueId, uuid, System.currentTimeMillis(), 0);

			// Store player object
			players.put(uuid, gPlayer);
			uuidMap.put(uniqueId, uuid);

			// Queue a player insert
			PlayerInsertQuery insertQuery = new PlayerInsertQuery(gPlayer);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

			return gPlayer;
		}
	}

	@Override
	public void onSelectionCompletion(List<GPlayer> returnList) throws SQLException {
		if (GlobalWarming.getInstance() != null) {
			new BukkitRunnable() {

				@Override
				public void run() {
					GlobalWarming.getInstance().getLogger().info("Loading " + returnList.size() + " players...");

					for (GPlayer gPlayer : returnList) {
						if (!uuidMap.containsKey(gPlayer.getUniqueId())) {
							uuidMap.put(gPlayer.getUniqueId(), gPlayer.getUuid());
						}

						if (!players.containsKey(gPlayer.getUuid())) {
							players.put(gPlayer.getUuid(), gPlayer);
						}
					}
				}
			}.runTask(GlobalWarming.getInstance());
		} else {
			System.out.println("Selection returned " + returnList.size() + " players.");
		}
	}
}
