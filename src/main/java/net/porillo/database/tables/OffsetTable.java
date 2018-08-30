package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.OffsetBounty;

import java.util.ArrayList;
import java.util.List;

public class OffsetTable extends Table {

	/**
	 * In memory storage of all available OffsetBounty
	 * When an offset bounty is complete, delete from this list
	 * On startup, query the offset table for available OffsetBounty's
	 */
	@Getter private List<OffsetBounty> offsetList = new ArrayList<>();

	public OffsetTable() {
		super("offsets");
		createIfNotExists();
	}

	@Override
	public void createIfNotExists() {
		String sql = "CREATE TABLE IF NOT EXISTS offsets (\n" +
				"  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
				"  creatorId VARCHAR(36) NOT NULL,\n" +
				"  hunterId VARCHAR(36),\n" +
				"  worldName VARCHAR(36) NOT NULL,\n" +
				"  logBlocksTarget INT,\n" +
				"  reward DOUBLE,\n" +
				"  timeStarted LONG,\n" +
				"  timeCompleted LONG\n" +
				");";
		CreateTableQuery createTableQuery = new CreateTableQuery(getTableName(), sql);
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
	}
}
