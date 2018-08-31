package net.porillo.database.queries.insert;

import net.porillo.database.api.InsertQuery;
import net.porillo.objects.Reduction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReductionInsertQuery extends InsertQuery {

	private Reduction reduction;

	public ReductionInsertQuery(Reduction reduction) {
		super("reductions");
		this.reduction = reduction;
	}

	public String getSQL() {
		return "INSERT INTO reductions (uniqueID, reductionerId, reductionKey, worldName, value) VALUES (?,?,?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setString(1, reduction.getUniqueID().toString());
		preparedStatement.setString(2, reduction.getReductioner().toString());
		preparedStatement.setString(3,
				reduction.getReductionKey() == null ? null : reduction.getReductionKey().toString());
		preparedStatement.setString(4, reduction.getWorldName());
		preparedStatement.setDouble(5, reduction.getReductionValue());
		return preparedStatement;
	}
}
