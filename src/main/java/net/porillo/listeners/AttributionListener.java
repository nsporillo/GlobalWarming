package net.porillo.listeners;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.PlayerFurnaceTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.objects.Furnace;
import net.porillo.objects.Player;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AttributionListener implements Listener {

	private GlobalWarming gw;

	public AttributionListener(GlobalWarming globalWarming) {
		this.gw = globalWarming;
	}

	/**
	 * Relate placed blocks to players
	 * @param event block place event
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.FURNACE) {
			PlayerFurnaceTable playerFurnaceTable = gw.getTableManager().getPlayerFurnaceTable();
			PlayerTable playerTable = gw.getTableManager().getPlayerTable();
			Map<Player, List<Furnace>> playerFurnaceMap = playerFurnaceTable.getPlayerFurnaceMap();

			Player player;

			if (playerTable.getPlayers().containsKey(event.getPlayer().getUniqueId())) {
				player = playerTable.getPlayers().get(event.getPlayer().getUniqueId());
			} else {
				player = new Player(event.getPlayer().getUniqueId(), System.currentTimeMillis(), 0);
			}

			Furnace furnace = new Furnace();
			furnace.setUniqueID(UUID.randomUUID());
			furnace.setLocation(event.getBlockPlaced().getLocation());
			furnace.setOwner(player);

			if (playerFurnaceMap.containsKey(player)) {
				List<Furnace> furnaces = playerFurnaceMap.get(player);
				furnaces.add(furnace);
				playerFurnaceMap.put(player, furnaces);
			} else {
				List<Furnace> furnaces = new ArrayList<>();
				furnaces.add(furnace);
				playerFurnaceMap.put(player, furnaces);
			}

			// TODO: Queue Async DB update
		}
	}

	/**
	 * Flag furnace as removed
	 * @param event
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.FURNACE) {
			PlayerFurnaceTable playerFurnaceTable = gw.getTableManager().getPlayerFurnaceTable();
			PlayerTable playerTable = gw.getTableManager().getPlayerTable();
			Map<Player, List<Furnace>> playerFurnaceMap = playerFurnaceTable.getPlayerFurnaceMap();

			Player player;

			if (playerTable.getPlayers().containsKey(event.getPlayer().getUniqueId())) {
				player = playerTable.getPlayers().get(event.getPlayer().getUniqueId());
			} else {
				player = new Player(event.getPlayer().getUniqueId(), System.currentTimeMillis(), 0);
			}

			if (playerFurnaceMap.containsKey(player)) {
				List<Furnace> furnaces = playerFurnaceMap.get(player);
				for (Furnace furnace : furnaces) {
					if (furnace.getLocation().equals(event.getBlock().getLocation())) {

					}
				}
				playerFurnaceMap.put(player, furnaces);
			} else {
				List<Furnace> furnaces = new ArrayList<>();
				furnaces.add(furnace);
				playerFurnaceMap.put(player, furnaces);
			}

			// TODO: Queue Async DB update
	}
}
