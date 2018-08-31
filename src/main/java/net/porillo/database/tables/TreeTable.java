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


	/**
	 * Handles all storage for tree collections
	 * @param tree updated tree object
	 */
	public void updateTree(Tree tree) {
		final GPlayer owner = tree.getOwner();

		// Update map of Gplayer -> Furnace Id
		if (playerMap.containsKey(owner)) {
			HashSet<Long> trees = playerMap.get(owner);
			trees.add(tree.getUniqueID());
			playerMap.put(owner, trees);
		} else {
			HashSet<Long> trees = new HashSet<>();
			trees.add(tree.getUniqueID());
			playerMap.put(owner, trees);
		}

		// Map the tree unique id to the Tree object
		treeMap.put(tree.getUniqueID(), tree);
		// Map the block location to the tree unique id
		locationMap.put(tree.getLocation(), tree.getUniqueID());
	}
}
