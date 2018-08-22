package net.porillo.database.api;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class DeleteQuery implements Query {

	private String table;

	@Override
	public String getTable() {
		return table;
	}

	@Override
	public String getQueryType() {
		return "delete";
	}
}
