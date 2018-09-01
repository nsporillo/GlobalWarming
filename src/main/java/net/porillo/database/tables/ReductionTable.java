package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.database.api.select.Selection;
import net.porillo.database.api.select.SelectionResult;
import net.porillo.objects.Reduction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReductionTable extends Table {

	@Getter private List<Reduction> reductions = new ArrayList<>();

	public ReductionTable() {
		super("reductions");
		createIfNotExists();
	}

	@Override
	public Selection makeSelectionQuery() {
		return null;
	}

	@Override
	public void onResultArrival(SelectionResult result) throws SQLException {

	}
}
