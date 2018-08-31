package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.GPlayer;
import net.porillo.objects.Tree;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TreeTable extends Table {

	/**
	 * Single source of truth for Tree objects
	 * Simply maps the furnace unique id to the tree object
	 */
	@Getter private Map<Long, Tree> treeMap = new HashMap<>();
	/**
	 * Helper collection to speed up event listeners that use locations
	 */
	@Getter private Map<Location, Long> locationMap = new HashMap<>();
	/**
	 * Helper collection to get every furnace a player is tied to
	 */
	@Getter private Map<GPlayer, HashSet<Long>> playerMap = new HashMap<>();

	public TreeTable() {
		super("trees");
		createIfNotExists();
	}

}
