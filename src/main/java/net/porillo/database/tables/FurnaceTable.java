package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Furnace;
import net.porillo.objects.GPlayer;
import org.bukkit.Location;

import java.util.*;

public class FurnaceTable extends Table {

	/**
	 * Single source of truth for Furnace objects
	 * Simply maps the furnace unique id to the furnace object
	 */
	@Getter private Map<Long, Furnace> furnaceMap = new HashMap<>();
	/**
	 * Helper collection to speed up event listeners that use locations
	 */
	@Getter private Map<Location, Long> locationMap = new HashMap<>();
	/**
	 * Helper collection to get every furnace a player is tied to
	 */
	@Getter private Map<GPlayer, HashSet<Long>> playerMap = new HashMap<>();

	public FurnaceTable() {
		super("furnaces");
		createIfNotExists();
	}

	/**
	 * Handles storage in all furnace collections
	 * @param furnace updated furnace object
	 */
	public void updateFurnace(Furnace furnace) {
		final GPlayer player = furnace.getOwner();

		// Update map of Gplayer -> Furnace Id
		if (!playerMap.containsKey(player)) {
			HashSet<Long> furnaces = new HashSet<>();
			furnaces.add(furnace.getUniqueID());
			playerMap.put(player, furnaces);
		} else {
			HashSet<Long> furnaces = playerMap.get(player);
			furnaces.add(furnace.getUniqueID());
			playerMap.put(player, furnaces);
		}

		// Map the furnace unique id to the furnace object
		furnaceMap.put(furnace.getUniqueID(), furnace);
		// Map the block location to the furnace unique id
		locationMap.put(furnace.getLocation(), furnace.getUniqueID());
	}
}
