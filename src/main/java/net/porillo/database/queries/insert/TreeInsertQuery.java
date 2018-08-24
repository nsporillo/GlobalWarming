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

	public static String getSQL() {
		return "INSERT INTO trees (uniqueID, ownerUUID, worldName, blockX, blockY, blockZ, sapling, size)" +
				" VALUES (?,?,?,?,?,?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setString(0, tree.getUniqueID().toString());
		preparedStatement.setString(1, tree.getOwner().getUuid().toString());
		preparedStatement.setString(2, tree.getLocation().getWorld().getName());
		preparedStatement.setInt(3, tree.getLocation().getBlockX());
		preparedStatement.setInt(4, tree.getLocation().getBlockY());
		preparedStatement.setInt(5, tree.getLocation().getBlockZ());
		preparedStatement.setBoolean(6, tree.isSapling());
		preparedStatement.setInt(7, tree.getSize());
		return preparedStatement;
	}
}
