package net.porillo.database.queries.insert;

import lombok.Getter;
import net.porillo.database.api.InsertQuery;
import net.porillo.objects.GPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerInsertQuery extends InsertQuery {

    @Getter
    private GPlayer player;

    public PlayerInsertQuery(GPlayer player) {
        super("players");
        this.player = player;
    }

    @Override
    public String getSQL() {
        return "INSERT INTO players (uniqueId, uuid, firstSeen, carbonScore, worldId) VALUES (?,?,?,?,?)";
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
        preparedStatement.setInt(1, player.getUniqueId());
        preparedStatement.setString(2, player.getUuid().toString());
        preparedStatement.setLong(3, player.getFirstSeen());
        preparedStatement.setInt(4, player.getCarbonScore());
        preparedStatement.setString(5, player.getWorldId().toString());
        return preparedStatement;
    }
}
