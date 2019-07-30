package net.porillo.database.api;

import lombok.Getter;
import lombok.ToString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@ToString
public abstract class SelectQuery<Type, Callback extends SelectCallback<Type>> implements Query {

    @Getter
    private String table;
    @Getter
    private Callback callback;

    public SelectQuery(String table, Callback callback) {
        this.table = table;
        this.callback = callback;
    }

    public abstract List<Type> queryDatabase(Connection connection) throws SQLException;

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(getSQL());
    }

    public void execute(Connection connection) throws SQLException {
        this.callback.onSelectionCompletion(queryDatabase(connection));
    }
}
