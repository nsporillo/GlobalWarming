package net.porillo.database.queries.insert;

import net.porillo.database.api.InsertQuery;
import net.porillo.objects.Contribution;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ContributionInsertQuery extends InsertQuery {

	private Contribution contribution;

	public ContributionInsertQuery(Contribution contribution) {
		super("contributions");
		this.contribution = contribution;
	}

	@Override
	public String getSQL() {
		return "INSERT INTO contributions (uniqueID, contributerId, contributionKey, worldName, value) VALUES (?,?,?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setLong(1, contribution.getUniqueID());
		preparedStatement.setLong(2, contribution.getContributer());
		preparedStatement.setLong(3, contribution.getContributionKey());
		preparedStatement.setString(4, contribution.getWorldName());
		preparedStatement.setDouble(5, contribution.getContributionValue());
		return preparedStatement;
	}

	@Override
	public Long getUniqueID() {
		return contribution.getUniqueID();
	}
}
