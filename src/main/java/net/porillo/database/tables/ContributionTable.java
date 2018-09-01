package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.api.select.Selection;
import net.porillo.database.api.select.SelectionResult;
import net.porillo.objects.Contribution;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContributionTable extends Table {

	@Getter private final List<Contribution> reductions = new ArrayList<>();

	public ContributionTable() {
		super("contributions");
		createIfNotExists();
		//loadTableData();
	}

	@Override
	public Selection makeSelectionQuery() {
		String sql = "";
		return new Selection(getTableName(), sql);
	}

	@Override
	public void onResultArrival(SelectionResult result) throws SQLException {
		/* Not currently loading this collection on startup
		if (result.getTableName().equals(getTableName())) {
			ResultSet rs = result.getResultSet();
			Long uniqueID = rs.getLong(1);
			Long contributerID = rs.getLong(2);
			Long contributionKey = rs.getLong(3);
			String worldName = rs.getString(4);
			Double value = rs.getDouble(5);
			Contribution contribution = new Contribution(uniqueID, contributerID, contributionKey, worldName, value);
		}
		*/
	}
}
