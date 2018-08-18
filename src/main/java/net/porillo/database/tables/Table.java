package net.porillo.database.tables;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public abstract class Table {

	private String tableName;

	public abstract void createIfNotExists();

	public abstract List<?> loadTable();
}
