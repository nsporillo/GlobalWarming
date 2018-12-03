package net.porillo.database.queries.delete;

import net.porillo.database.api.DeleteQuery;
import net.porillo.objects.Tree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TreeDeleteQuery extends DeleteQuery {

    private Tree tree;

    public TreeDeleteQuery(Tree tree) {
        super("trees");
        this.tree = tree;
    }

    @Override
    public String getSQL() {
        return "DELETE FROM trees WHERE uniqueId = ?";
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
        preparedStatement.setInt(1, tree.getUniqueId());
        return preparedStatement;
    }
}
