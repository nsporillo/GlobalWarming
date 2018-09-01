package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.select.Selection;
import net.porillo.database.api.select.SelectionResult;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Furnace;
import net.porillo.objects.GPlayer;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FurnaceTable extends Table {

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
		AsyncDBQueue.getInstance().queueSelection(makeSelectionQuery(), this);
	}

	/**
	 * Handles storage in all furnace collections
	 * @param furnace updated furnace object
	 */
	public void updateFurnace(Furnace furnace) {
		final GPlayer player = furnace.getOwner();

		// Update map of Gplayer -> Furnace Id
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
	public Selection makeSelectionQuery() {
		String sql = "SELECT * FROM furnaces WHERE active=true;";
		return new Selection(getTableName(), sql);
	}

	@Override
	public void onResultArrival(SelectionResult result) throws SQLException {
		if (result.getTableName().equals(getTableName())) {
			List<Furnace> furnaceList = new ArrayList<>();
			ResultSet rs = result.getResultSet();

			while (rs.next()) {
				furnaceList.add(new Furnace(rs));
			}

			new BukkitRunnable() {

				@Override
				public void run() {
					for (Furnace furnace : furnaceList) {
						updateFurnace(furnace);
					}
				}
			}.runTask(GlobalWarming.getInstance());
		}
	}
}
