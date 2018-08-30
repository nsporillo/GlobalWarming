package net.porillo.database;

import net.porillo.database.queries.insert.ContributionInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.ContributionTable;
import net.porillo.objects.Contribution;
import org.testng.annotations.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;

@Test
public class TableInsertDeleteTest extends TestBase {

	@Test(dataProvider = "mysqlDataSource")
	public void testContributionTable(String host, int port, String db, String user, String pass) throws SQLException, ClassNotFoundException {
		AsynchronousConnectionManager connectionManager = new AsynchronousConnectionManager(host, port, db, user, pass);
		new ContributionTable(); // make sure contribution table exists

		// Create a contribution and insert it into the DB
		final UUID uuid = randomUUID();
		Contribution contribution = new Contribution(uuid, randomUUID(), randomUUID(), "world", 15.0);
		AsyncDBQueue.getInstance().queueInsertQuery(new ContributionInsertQuery(contribution));
		AsyncDBQueue.getInstance().writeInsertQueue(connectionManager.openConnection());

		// Verify the object exists in the DB
		String select = "SELECT * FROM contributions WHERE uniqueID = ?";
		PreparedStatement insertStatement = connectionManager.openConnection().prepareStatement(select);
		insertStatement.setString(1, uuid.toString());
		ResultSet resultSet = insertStatement.executeQuery();

		// Validate the object is correct
		while (resultSet.next()) {
			assertThat("pk mismatch",
					uuid.equals(UUID.fromString(resultSet.getString(1))));
			assertThat("contributor mismatch",
					contribution.getContributer().equals(UUID.fromString(resultSet.getString(2))));
			assertThat("contribKey mismatch",
					contribution.getContributionKey().equals(UUID.fromString(resultSet.getString(3))));
			assertThat("world mismatch",
					contribution.getWorldName().equals(resultSet.getString(4)));
			assertThat("value mismatch",
					contribution.getContributionValue() == resultSet.getDouble(5));
		}

		// Delete the test object from the database
		String delete = "DELETE FROM contributions WHERE uniqueID = ?";
		PreparedStatement deleteStatement = connectionManager.openConnection().prepareStatement(delete);
		deleteStatement.setString(1, uuid.toString());
		deleteStatement.execute();
	}

	// TODO: Add tests for all other tables
	// Warning, some object constructors require bukkit api methods
	// which can be cumbersome to work with in a testing environment
}
