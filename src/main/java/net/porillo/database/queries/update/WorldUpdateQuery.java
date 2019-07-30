package net.porillo.database.queries.update;

import lombok.ToString;
import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.GWorld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@ToString
public class WorldUpdateQuery extends UpdateQuery<GWorld> {

    public WorldUpdateQuery(GWorld world) {
        super("worlds", world);
    }

    @Override
    public String getSQL() {
        return "UPDATE worlds SET carbonValue = ?, seaLevel = ?, size = ? WHERE uniqueId = ?";
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
        preparedStatement.setInt(1, getObject().getCarbonValue());
        preparedStatement.setInt(2, getObject().getSeaLevel());
        preparedStatement.setInt(3, getObject().getSize());
        preparedStatement.setInt(4, getObject().getUniqueID());
        return preparedStatement;
    }
}
