package net.porillo.database.queries;

import lombok.Getter;
import net.porillo.database.api.InsertQuery;
import net.porillo.objects.GPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerInsertQuery extends InsertQuery {

	@Getter private GPlayer player;

	public PlayerInsertQuery(GPlayer player) {
		super("players");
		this.player = player;
	}

	public static String getSQL() {
		return "INSERT INTO players (UniqueID, FirstSeen, CarbonScore) VALUES (?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setString(0, player.getUuid().toString());
		preparedStatement.setLong(1, player.getFirstSeen());
		preparedStatement.setInt(2, player.getCarbonScore());
		return preparedStatement;
	}
}
