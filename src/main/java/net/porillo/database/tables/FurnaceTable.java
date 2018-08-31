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

}
