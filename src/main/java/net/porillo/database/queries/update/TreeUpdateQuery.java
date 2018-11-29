package net.porillo.database.queries.update;

import net.porillo.database.api.UpdateQuery;
import net.porillo.objects.Tree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TreeUpdateQuery extends UpdateQuery<Tree> {

	public TreeUpdateQuery(Tree tree) {
		super("trees", tree);
	}

	@Override
	public String getSQL() {
		return "UPDATE trees SET sapling = ?, size = ? WHERE uniqueId = ?";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setBoolean(1, getObject().isSapling());
		preparedStatement.setInt(2, getObject().getSize());
		preparedStatement.setInt(3, getObject().getUniqueId());
		return preparedStatement;
	}
}
