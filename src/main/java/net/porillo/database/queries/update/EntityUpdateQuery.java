package net.porillo.database.queries.update;

import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.TrackedEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntityUpdateQuery extends UpdateQuery<TrackedEntity> {

	public EntityUpdateQuery(TrackedEntity entity) {
		super("entities", entity);
	}

	@Override
	public String getSQL() {
		return "UPDATE entities SET ticksLived = ?, alive = ? WHERE uniqueId = ?";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setLong(1, getObject().getTicksLived());
		preparedStatement.setBoolean(2, getObject().isAlive());
		preparedStatement.setInt(3, getObject().getUniqueId());
		return preparedStatement;
	}
}
