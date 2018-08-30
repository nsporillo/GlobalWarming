package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Furnace;
import net.porillo.objects.GPlayer;
import org.bukkit.Location;

import java.util.*;

public class FurnaceTable extends Table {

	/**
	 * Single source of truth for Furnace objects
	 * Simply maps the furnace unique id to the furnace object
	 */
	@Getter private Map<UUID, Furnace> furnaceMap = new HashMap<>();
	/**
	 * Helper collection to speed up event listeners that use locations
	 */
	@Getter private Map<Location, UUID> locationMap = new HashMap<>();
	/**
	 * Helper collection to get every furnace a player is tied to
	 */
	@Getter private Map<GPlayer, HashSet<UUID>> playerMap = new HashMap<>();

	public FurnaceTable() {
		super("furnaces");
		createIfNotExists();
	}

	@Override
	public void createIfNotExists() {
		String sql = "CREATE TABLE IF NOT EXISTS furnaces (\n" +
				"  uniqueID VARCHAR(36) NOT NULL,\n" +
				"  ownerUUID VARCHAR(36),\n" +
				"  worldName VARCHAR(255),\n" +
				"  blockX INT,\n" +
				"  blockY INT,\n" +
				"  blockZ INT,\n" +
				"  active BOOL,\n" +
				"  PRIMARY KEY (uniqueID)\n" +
				");";
		CreateTableQuery createTableQuery = new CreateTableQuery(getTableName(), sql);
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
	}

	public List<Furnace> loadTable() {
		return null;
	}
}
