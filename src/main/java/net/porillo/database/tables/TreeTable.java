package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.select.TreeSelectQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GPlayer;
import net.porillo.objects.Tree;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TreeTable extends Table implements SelectCallback<Tree> {

	/**
	 * Single source of truth for Tree objects
	 * Simply maps the furnace unique id to the tree object
	 */
	@Getter private Map<Integer, Tree> treeMap = new HashMap<>();
	/**
	 * Helper collection to speed up event listeners that use locations
	 */
	@Getter private Map<Location, Integer> locationMap = new HashMap<>();
	/**
	 * Helper collection to get every furnace a player is tied to
	 */
	@Getter private Map<GPlayer, HashSet<Integer>> playerMap = new HashMap<>();

	public TreeTable() {
		super("trees");
		createIfNotExists();

		TreeSelectQuery selectQuery = new TreeSelectQuery(this);
		AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
	}

	/**
	 * Handles all storage for tree collections
	 * @param tree updated tree object
	 */
	public void updateTree(Tree tree) {
		final GPlayer owner = tree.getOwner();

		// Update map of GPlayer -> Furnace Id
		if (playerMap.containsKey(owner)) {
			HashSet<Integer> trees = playerMap.get(owner);
			trees.add(tree.getUniqueID());
			playerMap.put(owner, trees);
		} else {
			HashSet<Integer> trees = new HashSet<>();
			trees.add(tree.getUniqueID());
			playerMap.put(owner, trees);
		}

		// Map the tree unique id to the Tree object
		treeMap.put(tree.getUniqueID(), tree);

		// Map the block location to the tree unique id
		locationMap.put(tree.getLocation(), tree.getUniqueID());
	}


	@Override
	public void onSelectionCompletion(List<Tree> returnList) throws SQLException {
		if (GlobalWarming.getInstance() != null) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Tree tree : returnList) {
						updateTree(tree);
					}
				}
			}.runTask(GlobalWarming.getInstance());
		} else {
			System.out.printf("Selection returned %d trees%n", returnList.size());
		}
	}
}
