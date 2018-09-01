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

	@Override
	public String getSQL() {
		return "INSERT INTO reductions (uniqueID, reductionerId, reductionKey, worldName, value) VALUES (?,?,?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setInt(1, reduction.getUniqueID());
		preparedStatement.setInt(2, reduction.getReductioner());
		preparedStatement.setInt(3, reduction.getReductionKey());
		preparedStatement.setString(4, reduction.getWorldName());
		preparedStatement.setInt(5, reduction.getReductionValue());
		return preparedStatement;
	}
}
