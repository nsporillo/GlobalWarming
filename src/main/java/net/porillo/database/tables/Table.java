package net.porillo.database.tables;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class Table {

	@Getter private String tableName;

	public abstract void createIfNotExists();
}
