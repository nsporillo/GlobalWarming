package net.porillo.database;

import net.porillo.database.queries.insert.WorldInsertQuery;
import net.porillo.database.queries.update.WorldUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GWorld;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;

@Test
public class TableUpdateTest {

	private Random random = new Random();

	@Test
	public void testWorldUpdate() throws SQLException, ClassNotFoundException {
		Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();
		AsyncDBQueue.getInstance().writeCreateTableQueue(connection);

		// Create a world and insert it into the DB
		final Integer uniqueId = random.nextInt();
		GWorld world = new GWorld(uniqueId, "testworld", 0.0, 0L, 0, 0, 0);
		AsyncDBQueue.getInstance().queueInsertQuery(new WorldInsertQuery(world));
		AsyncDBQueue.getInstance().writeInsertQueue(connection);

		// Verify the object exists in the DB
		String select = "SELECT * FROM worlds WHERE uniqueID = ?";
		PreparedStatement insertStatement = connection.prepareStatement(select);
		insertStatement.setLong(1, uniqueId);
		ResultSet resultSet = insertStatement.executeQuery();

		if (resultSet.last()) {
			assertThat("too many worlds", resultSet.getRow() == 1);
		}

		/*
		 * GlobalWarming listeners will update the object in memory and
		 * create a new update query object. Many updates to the same world or player
		 * can occur nearly simultaneously. We want to test that only 1 update query
		 * is actually executed against the Database.
		 */
		for (int i = 1; i <= 10; i++) {
			world.setCarbonValue(i * 1000);
			AsyncDBQueue.getInstance().queueUpdateQuery(new WorldUpdateQuery(world));
	}

		GWorld updateQueryWorld = (GWorld) AsyncDBQueue.getInstance().getUpdateQueue().peek().getObject();
		assertThat("world carbon score incorrect", updateQueryWorld.getCarbonValue().equals(10000));
		assertThat("queue has duplicates", AsyncDBQueue.getInstance().getUpdateQueue().size() == 1);
		AsyncDBQueue.getInstance().writeUpdateQueue(connection);

		// Delete the test object from the database
		String delete = "DELETE FROM worlds WHERE uniqueID = ?";
		PreparedStatement deleteStatement = connection.prepareStatement(delete);
		deleteStatement.setLong(1, uniqueId);
		deleteStatement.execute();
	}
}
