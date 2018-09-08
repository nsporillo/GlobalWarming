package net.porillo.database.queries.update;

import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.GWorld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WorldUpdateQuery extends UpdateQuery{

	private GWorld gWorld;

	public WorldUpdateQuery(GWorld world) {
		super("worlds");
		this.gWorld = world;
	}

	@Override
	public String getSQL() {
		return "UPDATE worlds SET carbonValue = ?, seaLevel = ?, size = ? WHERE uniqueId = ? ";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setInt(1, gWorld.getCarbonValue());
		preparedStatement.setInt(2, gWorld.getSeaLevel());
		preparedStatement.setInt(3, gWorld.getSize());
		preparedStatement.setInt(4, gWorld.getUniqueID());
		return preparedStatement;
	}
}
