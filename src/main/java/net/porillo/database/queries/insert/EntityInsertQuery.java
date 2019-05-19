package net.porillo.database.queries.insert;

import net.porillo.database.api.InsertQuery;
import net.porillo.objects.TrackedEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntityInsertQuery extends InsertQuery {

    private TrackedEntity entity;

    public EntityInsertQuery(TrackedEntity entity) {
        super("entities");
        this.entity = entity;
    }

    @Override
    public String getSQL() {
        return "INSERT INTO entities (uniqueId, uuid, breederId, entityType, ticksLived, alive) VALUES (?,?,?,?,?,?)";
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
        preparedStatement.setInt(1, entity.getUniqueId());
        preparedStatement.setString(2, entity.getUuid().toString());
        preparedStatement.setInt(3, entity.getBreederId());
        preparedStatement.setString(4, entity.getEntityType().name());
        preparedStatement.setLong(5, entity.getTicksLived());
        preparedStatement.setBoolean(6, entity.isAlive());
        return preparedStatement;
    }
}
