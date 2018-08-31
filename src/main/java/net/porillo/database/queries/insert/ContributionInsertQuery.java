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

	public String getSQL() {
		return "INSERT INTO contributions (uniqueID, contributerId, contributionKey, worldName, value) VALUES (?,?,?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setString(1, contribution.getUniqueID().toString());
		preparedStatement.setString(2, contribution.getContributer().toString());
		preparedStatement.setString(3,
				contribution.getContributionKey() == null ? null : contribution.getContributionKey().toString());
		preparedStatement.setString(4, contribution.getWorldName());
		preparedStatement.setDouble(5, contribution.getContributionValue());
		return preparedStatement;
	}
}
