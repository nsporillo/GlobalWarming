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
		return "UPDATE players SET carbonScore = ? WHERE uuid = ?";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setInt(1, gPlayer.getCarbonScore());
		preparedStatement.setString(2, gPlayer.getUuid().toString());
		return preparedStatement;
	}
}
