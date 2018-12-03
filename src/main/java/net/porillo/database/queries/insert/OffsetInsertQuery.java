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
        return "INSERT INTO offsets (uniqueId, creatorId, hunterId, worldId, logBlocksTarget, reward, timeStarted, timeCompleted)" +
              " VALUES (?,?,?,?,?,?,?,?)";
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
        preparedStatement.setInt(1, offsetBounty.getUniqueId());
        preparedStatement.setInt(2, offsetBounty.getCreatorId());

        if (offsetBounty.getHunterId() == null) {
            preparedStatement.setObject(3, null);
        } else {
            preparedStatement.setInt(3, offsetBounty.getHunterId());
        }

        preparedStatement.setString(4, offsetBounty.getWorldId().toString());
        preparedStatement.setInt(5, offsetBounty.getLogBlocksTarget());
        preparedStatement.setInt(6, offsetBounty.getReward());
        preparedStatement.setLong(7, offsetBounty.getTimeStarted());
        preparedStatement.setLong(8, offsetBounty.getTimeCompleted());
        return preparedStatement;
    }
}
