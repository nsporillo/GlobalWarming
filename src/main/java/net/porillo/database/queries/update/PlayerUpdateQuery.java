package net.porillo.database.queries.update;

import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.GPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerUpdateQuery extends UpdateQuery<GPlayer> {

	public PlayerUpdateQuery(GPlayer gPlayer) {
		super("players", gPlayer);

	}

	@Override
	public String getSQL() {
		return "UPDATE players SET carbonScore = ? WHERE uniqueID = ?";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setInt(1, getObject().getCarbonScore());
		preparedStatement.setInt(2, getObject().getUniqueId());
		return preparedStatement;
	}
}
