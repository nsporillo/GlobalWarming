package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.OffsetBounty;

import java.util.ArrayList;
import java.util.List;

public class OffsetTable extends Table {


	@Getter private List<OffsetBounty> offsetList = new ArrayList<>();

	public OffsetTable() {
		super("offsets");
	}

	@Override
	public void createIfNotExists() {

		CreateTableQuery createTableQuery = new CreateTableQuery(getTableName(), "");
		AsyncDBQueue.getInstance().executeCreateTable(createTableQuery);
	}
}
