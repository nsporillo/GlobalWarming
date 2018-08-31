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
		return "INSERT INTO trees (uniqueID, ownerUUID, worldName, blockX, blockY, blockZ, sapling, size)" +
				" VALUES (?,?,?,?,?,?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setString(1, tree.getUniqueID().toString());
		preparedStatement.setString(2, tree.getOwner().getUuid().toString());
		preparedStatement.setString(3, tree.getLocation().getWorld().getName());
		preparedStatement.setInt(4, tree.getLocation().getBlockX());
		preparedStatement.setInt(5, tree.getLocation().getBlockY());
		preparedStatement.setInt(6, tree.getLocation().getBlockZ());
		preparedStatement.setBoolean(7, tree.isSapling());
		preparedStatement.setInt(8, tree.getSize());
		return preparedStatement;
	}

	@Override
	public Long getUniqueID() {
		return tree.getUniqueID();
	}
}
