package net.porillo.database.queries.delete;

import net.porillo.database.api.DeleteQuery;
import net.porillo.objects.TrackedEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntityDeleteQuery extends DeleteQuery {

    private TrackedEntity entity;

    public EntityDeleteQuery(TrackedEntity entity) {
        super("entities");
        this.entity = entity;
    }

    @Override
    public String getSQL() {
        return "DELETE FROM entities WHERE uniqueId = ?";
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
        preparedStatement.setInt(1, entity.getUniqueId());
        return preparedStatement;
    }
}
