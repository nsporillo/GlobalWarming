package net.porillo.database;

import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;

@Test
public class TableCreationTest extends TestBase {

	private Connection connection;

	@BeforeTest
	public void before() throws SQLException, ClassNotFoundException {
		this.connection = getConnectionManager().openConnection();
	}

	@Test
	public void testPlayerTable() throws SQLException, ClassNotFoundException {
		dropTable("players");
		new PlayerTable(); // create table insert query, add it to the queue
		tableAssertions("players");
	}

	@Test
	public void testWorldTable() throws SQLException, ClassNotFoundException {
		dropTable("worlds");
		new WorldTable(); // create table insert query, add it to the queue
		tableAssertions("worlds");
	}

	@Test
	public void testFurnaceTable() throws SQLException, ClassNotFoundException {
		dropTable("furnaces");
		new FurnaceTable(); // create table insert query, add it to the queue
		tableAssertions("furnaces");
	}

	@Test
	public void testTreeTable() throws SQLException, ClassNotFoundException {
		dropTable("trees");
		new TreeTable(); // create table insert query, add it to the queue
		tableAssertions("trees");
	}

	@Test
	public void testContributionTable() throws SQLException, ClassNotFoundException {
		dropTable("contributions");
		new ContributionTable(); // create table insert query, add it to the queue
		tableAssertions("contributions");
	}

	@Test
	public void testReductionTable() throws SQLException, ClassNotFoundException {
		dropTable("reductions");
		new ReductionTable(); // create table insert query, add it to the queue
		tableAssertions("reductions");
	}

	@Test
	public void testOffsetTable() throws SQLException, ClassNotFoundException {
		dropTable("offsets");
		new OffsetTable(); // create table insert query, add it to the queue
		tableAssertions("offsets");
	}

	private void dropTable(String table) {
		try {
			connection.createStatement().executeUpdate("DROP TABLE IF EXISTS " + table);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void tableAssertions(String table) throws SQLException, ClassNotFoundException {
		// Assert that the queue has an insert query
		assertThat("CreateQueue doesn't have query", AsyncDBQueue.getInstance().getCreateQueue().size() == 1);

		// Assert that the queue has the correct table insert query
		CreateTableQuery createTableQuery = AsyncDBQueue.getInstance().getCreateQueue().peek();
		assertThat("CreateQueue contains wrong table", createTableQuery.getTable().equals(table));

		// Since this is just a test, write the db queue on the same thread
		AsyncDBQueue.getInstance().writeCreateTableQueue(connection);

		// Verify the table is created, if the query fails then the table does not exist
		connection.createStatement().execute("SELECT 1 FROM " + table + " LIMIT 1");
	}
}
