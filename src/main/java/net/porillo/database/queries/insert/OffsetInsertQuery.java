package net.porillo.database.queries.insert;

import net.porillo.database.api.InsertQuery;
import net.porillo.objects.OffsetBounty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OffsetInsertQuery extends InsertQuery {

	private OffsetBounty offsetBounty;

	public OffsetInsertQuery(OffsetBounty offsetBounty) {
		super("offsets");
		this.offsetBounty = offsetBounty;
	}

	public static String getSQL() {
		return "INSERT INTO offsets (creatorId, hunterId, worldName, logBlocksTarget, reward, timeStarted, timeCompleted)" +
				" VALUES (?,?,?,?,?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setString(0, offsetBounty.getCreator().getUuid().toString());

		if (offsetBounty.getHunter() == null) {
			preparedStatement.setString(1, null);
		} else {
			preparedStatement.setString(1, offsetBounty.getHunter().getUuid().toString());
		}

		preparedStatement.setString(2, offsetBounty.getWorld().getWorldName());
		preparedStatement.setInt(3, offsetBounty.getLogBlocksTarget());
		preparedStatement.setDouble(4, offsetBounty.getReward());
		preparedStatement.setLong(5, offsetBounty.getTimeStarted());
		preparedStatement.setLong(6, offsetBounty.getTimeCompleted());
		return preparedStatement;
	}
}
