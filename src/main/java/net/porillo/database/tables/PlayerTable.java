package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.select.Selection;
import net.porillo.database.api.select.SelectionResult;
import net.porillo.database.queries.insert.PlayerInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerTable extends Table {

	@Getter private Map<UUID, GPlayer> players = new HashMap<>();

	public PlayerTable() {
		super("players");
		createIfNotExists();
		AsyncDBQueue.getInstance().queueSelection(makeSelectionQuery(), this);
	}

	public GPlayer getOrCreatePlayer(UUID uuid, boolean untracked) {
		if (players.containsKey(uuid)) {
			return players.get(uuid);
		} else {
			// Create new player object
			Integer uniqueId = untracked ? 0 : GlobalWarming.getInstance().getRandom().nextInt();
			GPlayer gPlayer = new GPlayer(uniqueId, uuid, System.currentTimeMillis(), 0);

			// Store player object
			players.put(uuid, gPlayer);

			// Queue a player insert
			PlayerInsertQuery insertQuery = new PlayerInsertQuery(gPlayer);
			AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

			return gPlayer;
		}
	}

	@Override
	public Selection makeSelectionQuery() {
		String sql = "SELECT * FROM players;";
		return new Selection(getTableName(), sql);
	}

	@Override
	public void onResultArrival(SelectionResult result) throws SQLException {
		List<GPlayer> gPlayerList = new ArrayList<>();
		ResultSet rs = result.getResultSet();

		try {
			while(rs.next()) {
				//gPlayerList.add(new GPlayer(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (result.getTableName().equals(getTableName())) {
			new BukkitRunnable(){

				@Override
				public void run() {
					for (GPlayer gPlayer : gPlayerList) {
						//players.put(gPlayer.getUuid(), gPlayer);
					}
				}
			}.runTask(GlobalWarming.getInstance());
		}
	}
}
