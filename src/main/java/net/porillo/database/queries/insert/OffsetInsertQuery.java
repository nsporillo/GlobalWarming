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

	@Override
	public String getSQL() {
		return "INSERT INTO offsets (creatorId, hunterId, worldName, logBlocksTarget, reward, timeStarted, timeCompleted)" +
				" VALUES (?,?,?,?,?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setLong(1, offsetBounty.getCreator().getUniqueId());

		if (offsetBounty.getHunter() != null) {
			preparedStatement.setInt(2, offsetBounty.getHunter().getUniqueId());
		} else {
			preparedStatement.setObject(2, null);
		}

		preparedStatement.setString(3, offsetBounty.getWorld().getWorldName());
		preparedStatement.setInt(4, offsetBounty.getLogBlocksTarget());
		preparedStatement.setInt(5, offsetBounty.getReward());
		preparedStatement.setLong(6, offsetBounty.getTimeStarted());
		preparedStatement.setLong(7, offsetBounty.getTimeCompleted());
		return preparedStatement;
	}
}
