package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.queries.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Furnace;

import net.porillo.objects.GPlayer;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FurnaceTable extends Table {

	@Getter private Map<Location, Furnace> locationMap = new HashMap<>();
	@Getter private Map<GPlayer, HashSet<Furnace>> playerMap = new HashMap<>();

	public FurnaceTable() {
		super("furnaces");
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
				"  PRIMARY KEY (uniqueID)\n" +
				")";
		CreateTableQuery createTableQuery = new CreateTableQuery("furnaces", sql);
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
	}

    public void addWorld(World world) {

	}

	public List<Furnace> loadTable() {
		return null;
	}
}
