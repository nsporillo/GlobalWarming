package net.porillo.database;

import net.porillo.database.queries.insert.ContributionInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.ContributionTable;
import net.porillo.objects.Contribution;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;

@Test
public class TableInsertDeleteTest extends TestBase {

	private Connection connection;
	private Random random = new Random();

	@BeforeClass
	public void before() throws SQLException, ClassNotFoundException {
		this.connection = getConnectionManager().openConnection();
	}

	public void testContributionTable() throws SQLException, ClassNotFoundException {
		new ContributionTable(); // make sure contribution table exists

		// Create a contribution and insert it into the DB
		final Integer uniqueId = random.nextInt();
		Contribution contribution = new Contribution(uniqueId, random.nextInt(), random.nextInt(), "world", 15);
		AsyncDBQueue.getInstance().queueInsertQuery(new ContributionInsertQuery(contribution));
		AsyncDBQueue.getInstance().writeInsertQueue(connection);

		// Verify the object exists in the DB
		String select = "SELECT * FROM contributions WHERE uniqueID = ?";
		PreparedStatement insertStatement = connection.prepareStatement(select);
		insertStatement.setLong(1, uniqueId);
		ResultSet resultSet = insertStatement.executeQuery();

		// Validate the object is correct
		while (resultSet.next()) {
			assertThat("pk mismatch",
					uniqueId.equals(resultSet.getInt(1)));
			assertThat("contributor mismatch",
					contribution.getContributer().equals(resultSet.getInt(2)));
			assertThat("contribKey mismatch",
					contribution.getContributionKey().equals(resultSet.getInt(3)));
			assertThat("world mismatch",
					contribution.getWorldName().equals(resultSet.getString(4)));
			assertThat("value mismatch",
					contribution.getContributionValue() == resultSet.getInt(5));
		}

		// Delete the test object from the database
		String delete = "DELETE FROM contributions WHERE uniqueID = ?";
		PreparedStatement deleteStatement = connection.prepareStatement(delete);
		deleteStatement.setLong(1, uniqueId);
		deleteStatement.execute();
	}

	// TODO: Add tests for all other tables
	// Warning, some object constructors require bukkit api methods
	// which can be cumbersome to work with in a testing environment
}
