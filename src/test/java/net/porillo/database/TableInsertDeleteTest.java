package net.porillo.database;

import net.porillo.database.queries.insert.ContributionInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.ContributionTable;
import net.porillo.objects.Contribution;
import org.testng.annotations.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;

@Test
public class TableInsertDeleteTest extends TestBase {

	private Random random = new Random();

	@Test(dataProvider = "mysqlDataSource")
	public void testContributionTable(String host, int port, String db, String user, String pass) throws SQLException, ClassNotFoundException {
		ConnectionManager connectionManager = new ConnectionManager(host, port, db, user, pass);
		new ContributionTable(); // make sure contribution table exists

		// Create a contribution and insert it into the DB
		final Long uniqueId = random.nextLong();
		Contribution contribution = new Contribution(uniqueId, random.nextLong(), random.nextLong(), "world", 15.0);
		AsyncDBQueue.getInstance().queueInsertQuery(new ContributionInsertQuery(contribution));
		AsyncDBQueue.getInstance().writeInsertQueue(connectionManager.openConnection());

		// Verify the object exists in the DB
		String select = "SELECT * FROM contributions WHERE uniqueID = ?";
		PreparedStatement insertStatement = connectionManager.openConnection().prepareStatement(select);
		insertStatement.setLong(1, uniqueId);
		ResultSet resultSet = insertStatement.executeQuery();

		// Validate the object is correct
		while (resultSet.next()) {
			assertThat("pk mismatch",
					uniqueId.equals(resultSet.getLong(1)));
			assertThat("contributor mismatch",
					contribution.getContributer().equals(resultSet.getLong(2)));
			assertThat("contribKey mismatch",
					contribution.getContributionKey().equals(resultSet.getLong(3)));
			assertThat("world mismatch",
					contribution.getWorldName().equals(resultSet.getString(4)));
			assertThat("value mismatch",
					contribution.getContributionValue() == resultSet.getDouble(5));
		}

		// Delete the test object from the database
		String delete = "DELETE FROM contributions WHERE uniqueID = ?";
		PreparedStatement deleteStatement = connectionManager.openConnection().prepareStatement(delete);
		deleteStatement.setLong(1, uniqueId);
		deleteStatement.execute();
	}

	// TODO: Add tests for all other tables
	// Warning, some object constructors require bukkit api methods
	// which can be cumbersome to work with in a testing environment
}
