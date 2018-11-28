package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.select.FurnaceSelectQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Furnace;
import net.porillo.objects.GPlayer;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FurnaceTable extends Table implements SelectCallback<Furnace> {

	/**
	 * Single source of truth for Furnace objects
	 * Simply maps the furnace unique id to the furnace object
	 */
	@Getter private Map<Integer, Furnace> furnaceMap = new HashMap<>();
	/**
	 * Helper collection to speed up event listeners that use locations
	 */
	@Getter private Map<Location, Integer> locationMap = new HashMap<>();
	/**
	 * Helper collection to get every furnace a player is tied to
	 */
	@Getter private Map<GPlayer, HashSet<Integer>> playerMap = new HashMap<>();

	public FurnaceTable() {
		super("furnaces");
		createIfNotExists();

		FurnaceSelectQuery selectQuery = new FurnaceSelectQuery(this);
		AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
	}

	/**
	 * Handles storage in all furnace collections
	 * @param furnace updated furnace object
	 */
	public void updateFurnace(Furnace furnace) {
		final GPlayer player = furnace.getOwner();

		// Update map of GPlayer -> Furnace Id
		if (!playerMap.containsKey(player)) {
			HashSet<Integer> furnaces = new HashSet<>();
			furnaces.add(furnace.getUniqueID());
			playerMap.put(player, furnaces);
		} else {
			HashSet<Integer> furnaces = playerMap.get(player);
			furnaces.add(furnace.getUniqueID());
			playerMap.put(player, furnaces);
		}

		// Map the furnace unique id to the furnace object
		furnaceMap.put(furnace.getUniqueID(), furnace);

		// Map the block location to the furnace unique id
		locationMap.put(furnace.getLocation(), furnace.getUniqueID());
	}

	@Override
	public void onSelectionCompletion(List<Furnace> returnList) throws SQLException {
		if (GlobalWarming.getInstance() != null) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Furnace furnace : returnList) {
						updateFurnace(furnace);
					}
				}
			}.runTask(GlobalWarming.getInstance());
		} else {
			System.out.printf("Selection returned %d furnaces.%n", returnList.size());
		}
	}
}
