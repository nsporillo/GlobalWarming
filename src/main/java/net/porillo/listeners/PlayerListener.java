package net.porillo.listeners;

import lombok.AllArgsConstructor;
import net.porillo.GlobalWarming;
import net.porillo.database.queries.insert.PlayerInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.PlayerTable;
import net.porillo.objects.GPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class PlayerListener implements Listener {

	private GlobalWarming gw;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		PlayerTable table = gw.getTableManager().getPlayerTable();

		if (!table.getPlayers().containsKey(event.getPlayer().getUniqueId())) {
			Long uniqueId = GlobalWarming.getInstance().getRandom().nextLong();
			GPlayer player = new GPlayer(uniqueId, event.getPlayer().getUniqueId(), System.currentTimeMillis(), 0);

			table.getPlayers().put(event.getPlayer().getUniqueId(), player);
			AsyncDBQueue.getInstance().queueInsertQuery(new PlayerInsertQuery(player));
		}
	}
}
