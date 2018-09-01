package net.porillo.database.api.select;

import java.sql.SQLException;

public interface SelectionListener {

	void onResultArrival(SelectionResult result) throws SQLException;
}
