package net.porillo.database.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class UpdateQuery<T> implements Query {

	@Getter private String table;
	@Getter private T object;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UpdateQuery<?> that = (UpdateQuery<?>) o;

		return table.equals(that.table) && object.equals(that.object);
	}

	@Override
	public int hashCode() {
		int result = table.hashCode();
		result = 31 * result + object.hashCode();
		return result;
	}
}
