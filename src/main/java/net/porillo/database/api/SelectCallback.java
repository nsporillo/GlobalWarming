package net.porillo.database.api;

import java.util.List;

public interface SelectCallback<Type> {

	void onSelectionCompletion(List<Type> returnList);
}
