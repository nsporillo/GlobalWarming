package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GWorld;

import java.util.ArrayList;
import java.util.List;

public class WorldTable extends Table {

    @Getter
    private List<GWorld> worlds = new ArrayList<>();

	public WorldTable() {
        super("worlds");
        createIfNotExists();
	}

	@Override
	public void createIfNotExists() {
		String sql = "CREATE TABLE IF NOT EXISTS worlds (\n" +
				"  worldName VARCHAR(36) NOT NULL,\n" +
				"  firstSeen LONG,\n" +
				"  carbonValue INT,\n" +
				"  seaLevel INT,\n" +
				"  size INT,\n" +
				"  PRIMARY KEY (worldName)\n" +
				");";
		CreateTableQuery createTableQuery = new CreateTableQuery(getTableName(), sql);
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
	}

    public GWorld getWorld(String name) {
        for (GWorld world : worlds) {
            if (world.getWorldName().equals(name)) {
                return world;
			}
		}

		return null;
	}

    public void addWorld(GWorld world) {
        this.worlds.add(world);
    }

    public List<GWorld> loadTable() {
		return null;
	}
}
