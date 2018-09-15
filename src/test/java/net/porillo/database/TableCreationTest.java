package net.porillo.database;

import net.porillo.database.queue.AsyncDBQueue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class TableCreationTest {

	@BeforeClass
	public void dropTables() {
		dropTable("players");
		dropTable("worlds");
		dropTable("furnaces");
		dropTable("trees");
		dropTable("contributions");
		dropTable("reductions");
		dropTable("offsets");
	}

	@Test
	public void testTableCreation() throws SQLException, ClassNotFoundException {
		TableManager tableManager = TableManager.getInstance();
		Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();
		AsyncDBQueue.getInstance().writeCreateTableQueue(connection);

		tableAssertions("players");
		tableAssertions("worlds");
		tableAssertions("furnaces");
		tableAssertions("trees");
		tableAssertions("contributions");
		tableAssertions("reductions");
		tableAssertions("offsets");
	}

	private void dropTable(String table) {
		try {
			Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();
			connection.createStatement().executeUpdate("DROP TABLE IF EXISTS " + table);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void tableAssertions(String table) throws SQLException, ClassNotFoundException {
		Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();

		// Verify the table is created, if the query fails then the table does not exist
		connection.createStatement().execute("SELECT 1 FROM " + table + " LIMIT 1");
	}
}
