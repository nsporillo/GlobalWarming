package net.porillo.database.api;

import lombok.Getter;
import lombok.ToString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Getter
@ToString
public abstract class SelectQuery<Type, Callback extends SelectCallback<Type>> implements Query {

    private String table;
    private Callback callback;

    public SelectQuery(String table, Callback callback) {
        this.table = table;
        this.callback = callback;
    }

    public abstract List<Type> queryDatabase(Connection connection) throws SQLException;

    public void execute(Connection connection) throws SQLException {
        this.callback.onSelectionCompletion(queryDatabase(connection));
    }
}
