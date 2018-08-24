package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Contribution;

import java.util.ArrayList;
import java.util.List;

public class ContributionTable extends Table {

	@Getter  private List<Contribution> reductions = new ArrayList<>();

	public ContributionTable() {
		super("contributions");
		createIfNotExists();
	}

	@Override
	public void createIfNotExists() {
		String sql = "CREATE TABLE IF NOT EXISTS contributions (\n" +
				"  uniqueID VARCHAR(36) NOT NULL,\n" +
				"  contributerId VARCHAR(36),\n" +
				"  contributionKey VARCHAR(36),\n" +
				"  worldName VARCHAR(255),\n" +
				"  value DOUBLE,\n" +
				"  PRIMARY KEY (uniqueID)\n" +
				")";
		CreateTableQuery createTableQuery = new CreateTableQuery("contributions", sql);
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
	}

	public List<Contribution> loadTable() {
		return null;
	}
}
