package net.porillo.database.queries.update;

import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.OffsetBounty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OffsetUpdateQuery extends UpdateQuery {

	private OffsetBounty offsetBounty;

	public OffsetUpdateQuery(OffsetBounty offsetBounty) {
		super("offsets");
		this.offsetBounty = offsetBounty;
	}

	@Override
	public String getSQL() {
		return "UPDATE offsets SET hunter = ?, timeCompleted = ? WHERE id = ?";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());

		if (offsetBounty.getHunter() == null) {
			//preparedStatement.setLong(1, 0);
		} else {
			preparedStatement.setLong(1, offsetBounty.getHunter().getUniqueId());
		}

		preparedStatement.setLong(2, offsetBounty.getTimeCompleted());
		preparedStatement.setLong(3, offsetBounty.getUniqueId());

		return preparedStatement;
	}

	@Override
	public Long getUniqueID() {
		return offsetBounty.getUniqueId();
	}
}
