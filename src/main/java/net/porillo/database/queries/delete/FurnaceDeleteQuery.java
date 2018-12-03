package net.porillo.database.queries.delete;

import net.porillo.database.api.DeleteQuery;
import net.porillo.objects.Furnace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FurnaceDeleteQuery extends DeleteQuery {

    private Furnace furnace;

    public FurnaceDeleteQuery(Furnace furnace) {
        super("furnaces");
        this.furnace = furnace;
    }

    @Override
    public String getSQL() {
        return "DELETE FROM furnaces WHERE uniqueId = ?";
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
        preparedStatement.setInt(1, furnace.getUniqueId());
        return preparedStatement;
    }
}
