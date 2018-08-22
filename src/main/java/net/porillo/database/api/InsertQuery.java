package net.porillo.database.api;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class InsertQuery implements Query {

	private String table;

	@Override
	public String getTable() {
		return table;
	}

	@Override
	public String getQueryType() {
		return "insert";
	}
}
