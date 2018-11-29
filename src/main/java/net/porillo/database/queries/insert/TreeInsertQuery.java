package net.porillo.database.queries.insert;

import net.porillo.database.api.InsertQuery;
import net.porillo.objects.Tree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TreeInsertQuery extends InsertQuery {

    private Tree tree;

    public TreeInsertQuery(Tree tree) {
        super("trees");
        this.tree = tree;
    }

    @Override
    public String getSQL() {
        return "INSERT INTO trees (uniqueId, ownerId, worldId, blockX, blockY, blockZ, sapling, size)" +
              " VALUES (?,?,?,?,?,?,?,?)";
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
        preparedStatement.setInt(1, tree.getUniqueId());
        preparedStatement.setInt(2, tree.getOwnerId());
        preparedStatement.setString(3, tree.getLocation().getWorld().getUID().toString());
        preparedStatement.setInt(4, tree.getLocation().getBlockX());
        preparedStatement.setInt(5, tree.getLocation().getBlockY());
        preparedStatement.setInt(6, tree.getLocation().getBlockZ());
        preparedStatement.setBoolean(7, tree.isSapling());
        preparedStatement.setInt(8, tree.getSize());
        return preparedStatement;
    }
}
