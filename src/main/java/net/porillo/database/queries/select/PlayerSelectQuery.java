package net.porillo.database.queries.select;

import net.porillo.database.api.SelectQuery;
import net.porillo.database.tables.PlayerTable;
import net.porillo.objects.GPlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerSelectQuery extends SelectQuery<GPlayer, PlayerTable> {

    public PlayerSelectQuery(PlayerTable callback) {
        super("players", callback);
    }

    @Override
    public String getSQL() {
        return "SELECT * FROM players";
    }

    @Override
    public List<GPlayer> queryDatabase(Connection connection) throws SQLException {
        List<GPlayer> gPlayers = new ArrayList<>();
        ResultSet rs = prepareStatement(connection).executeQuery(getSQL());

        while (rs.next()) {
            gPlayers.add(new GPlayer(rs));
        }

        return gPlayers;
    }
}
