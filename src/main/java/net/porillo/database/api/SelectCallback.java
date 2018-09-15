package net.porillo.database.api;

import java.sql.SQLException;
import java.util.List;

public interface SelectCallback<Type> {

	void onSelectionCompletion(List<Type> returnList) throws SQLException;
}
