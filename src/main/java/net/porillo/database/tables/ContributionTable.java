package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Contribution;

import java.util.ArrayList;
import java.util.List;

public class ContributionTable extends Table {

	@Getter private List<Contribution> reductions = new ArrayList<>();

	public ContributionTable() {
		super("contributions");
		createIfNotExists();
	}

	@Override
	public void createIfNotExists() {
		CreateTableQuery createTableQuery = new CreateTableQuery(getTableName(), loadSQLFromFile());
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
	}

	public List<Contribution> loadTable() {
		return null;
	}
}
