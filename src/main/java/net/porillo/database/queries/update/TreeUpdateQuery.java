package net.porillo.database.queries.update;

import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.Tree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TreeUpdateQuery extends UpdateQuery {

	private Tree tree;

	public TreeUpdateQuery(Tree tree) {
		super("trees");
		this.tree = tree;
	}

	@Override
	public String getSQL() {
		return "UPDATE trees SET sapling = ?, size = ? WHERE uniqueId = ?";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setBoolean(1, tree.isSapling());
		preparedStatement.setInt(2, tree.getSize());
		preparedStatement.setInt(3, tree.getUniqueID());
		return preparedStatement;
	}
}
