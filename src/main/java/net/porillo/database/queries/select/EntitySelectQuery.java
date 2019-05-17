package net.porillo.database.queries.select;

import net.porillo.database.api.SelectQuery;
import net.porillo.database.tables.EntityTable;
import net.porillo.objects.TrackedEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntitySelectQuery extends SelectQuery<TrackedEntity, EntityTable> {

    public EntitySelectQuery(EntityTable callback) {
        super("entities", callback);
    }

    @Override
    public List<TrackedEntity> queryDatabase(Connection connection) throws SQLException {
        List<TrackedEntity> trackedEntities = new ArrayList<>();
        ResultSet rs = prepareStatement(connection).executeQuery(getSQL());

        while (rs.next()) {
            trackedEntities.add(new TrackedEntity(rs));
        }

        return trackedEntities;
    }

    @Override
    public String getSQL() {
        return "SELECT * FROM entities WHERE alive = true";
    }
}