package net.porillo.database.queries.update;

import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.Furnace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FurnaceUpdateQuery extends UpdateQuery<Furnace> {

	public FurnaceUpdateQuery(Furnace furnace) {
		super("furnaces", furnace);
	}

	@Override
	public String getSQL() {
		return "UPDATE furnaces SET active = ? WHERE uniqueId = ?";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setBoolean(1, getObject().isActive());
		preparedStatement.setInt(2, getObject().getUniqueID());
		return preparedStatement;
	}
}
