package net.porillo.database.queries.update;

import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.OffsetBounty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OffsetUpdateQuery extends UpdateQuery<OffsetBounty> {

	public OffsetUpdateQuery(OffsetBounty offsetBounty) {
		super("offsets", offsetBounty);
	}

	@Override
	public String getSQL() {
		return "UPDATE offsets SET hunterId = ?, timeStarted = ?, timeCompleted = ? WHERE uniqueId = ?";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		if (getObject().getHunterId() == null) {
			preparedStatement.setObject(1, null);
		} else {
			preparedStatement.setInt(1, getObject().getHunterId());
		}

        preparedStatement.setLong(2, getObject().getTimeStarted());
		preparedStatement.setLong(3, getObject().getTimeCompleted());
		preparedStatement.setInt(4, getObject().getUniqueId());
		return preparedStatement;
	}
}
