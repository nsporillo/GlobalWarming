package net.porillo.database.queries.update;

import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.GPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerUpdateQuery extends UpdateQuery {

	private GPlayer gPlayer;

	public PlayerUpdateQuery(GPlayer gPlayer) {
		super("players");
		this.gPlayer = gPlayer;
	}

	@Override
	public String getSQL() {
		return "UPDATE players SET carbonScore = ? WHERE uniqueID = ?";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setInt(1, gPlayer.getCarbonScore());
		preparedStatement.setLong(2, gPlayer.getUniqueId());
		return preparedStatement;
	}

	@Override
	public Long getUniqueID() {
		return gPlayer.getUniqueId();
	}
}
