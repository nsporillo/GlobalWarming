package net.porillo.database.api.select;

public abstract class GeneralSelection extends Selection implements SelectionListener {

	public GeneralSelection(String tableName, String sql) {
		super(tableName, sql);
	}
}
