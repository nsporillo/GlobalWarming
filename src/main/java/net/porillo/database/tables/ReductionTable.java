package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Reduction;

import java.util.ArrayList;
import java.util.List;

public class ReductionTable extends Table {

	@Getter  private List<Reduction> reductions = new ArrayList<>();

	public ReductionTable() {
		super("reductions");
		createIfNotExists();
	}

	@Override
	public void createIfNotExists() {
		String sql = "CREATE TABLE IF NOT EXISTS reductions (\n" +
				"  uniqueID VARCHAR(36) NOT NULL,\n" +
				"  reductionerId VARCHAR(36),\n" +
				"  reductionKey VARCHAR(36),\n" +
				"  worldName VARCHAR(255),\n" +
				"  value DOUBLE,\n" +
				"  PRIMARY KEY (uniqueID)\n" +
				");";
		CreateTableQuery createTableQuery = new CreateTableQuery(getTableName(), sql);
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
	}

	public List<Reduction> loadTable() {
		return null;
	}
}
