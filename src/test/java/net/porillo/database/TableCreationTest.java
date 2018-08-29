package net.porillo.database;

import net.porillo.database.queries.other.CreateTableQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.*;
import org.junit.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.SQLException;

@Test
public class TableCreationTest {

	/**
	 * Configures this test to use the default data source such that
	 * these tests will pass when ran on the Jenkins server. A user
	 * and pass that is only permitted to execute locally is setup.
	 *
	 * @return 2d array of datasource credentials
	 */
	@DataProvider(name = "mysqlDataSource")
	public static Object[][] createTestDataSource() {
		return new Object[][]{
				{"localhost", 3306, "GlobalWarming", "jenkins", "tests"}
		};
	}

	@Test(dataProvider = "mysqlDataSource")
	public void testContributionTable(String host, int port, String db, String user, String pass) throws SQLException, ClassNotFoundException {
		AsynchronousConnectionManager connectionManager = new AsynchronousConnectionManager(host, port, db, user, pass);
		new ContributionTable(); // create table insert query, add it to the queue
		tableAssertions(connectionManager, "contributions");
	}

	@Test(dataProvider = "mysqlDataSource")
	public void testReductionTable(String host, int port, String db, String user, String pass) throws SQLException, ClassNotFoundException {
		AsynchronousConnectionManager connectionManager = new AsynchronousConnectionManager(host, port, db, user, pass);
		new ReductionTable(); // create table insert query, add it to the queue
		tableAssertions(connectionManager, "reductions");
	}

	@Test(dataProvider = "mysqlDataSource")
	public void testFurnaceTable(String host, int port, String db, String user, String pass) throws SQLException, ClassNotFoundException {
		AsynchronousConnectionManager connectionManager = new AsynchronousConnectionManager(host, port, db, user, pass);
		new FurnaceTable(); // create table insert query, add it to the queue
		tableAssertions(connectionManager, "furnaces");
	}

	@Test(dataProvider = "mysqlDataSource")
	public void testTreeTable(String host, int port, String db, String user, String pass) throws SQLException, ClassNotFoundException {
		AsynchronousConnectionManager connectionManager = new AsynchronousConnectionManager(host, port, db, user, pass);
		new TreeTable(); // create table insert query, add it to the queue
		tableAssertions(connectionManager, "trees");
	}

	@Test(dataProvider = "mysqlDataSource")
	public void testOffsetTable(String host, int port, String db, String user, String pass) throws SQLException, ClassNotFoundException {
		AsynchronousConnectionManager connectionManager = new AsynchronousConnectionManager(host, port, db, user, pass);
		new OffsetTable(); // create table insert query, add it to the queue
		tableAssertions(connectionManager, "offsets");
	}

	@Test(dataProvider = "mysqlDataSource")
	public void testPlayerTable(String host, int port, String db, String user, String pass) throws SQLException, ClassNotFoundException {
		AsynchronousConnectionManager connectionManager = new AsynchronousConnectionManager(host, port, db, user, pass);
		new PlayerTable(); // create table insert query, add it to the queue
		tableAssertions(connectionManager, "players");
	}

	@Test(dataProvider = "mysqlDataSource")
	public void testWorldTable(String host, int port, String db, String user, String pass) throws SQLException, ClassNotFoundException {
		AsynchronousConnectionManager connectionManager = new AsynchronousConnectionManager(host, port, db, user, pass);
		new WorldTable(); // create table insert query, add it to the queue
		tableAssertions(connectionManager, "worlds");
	}

	private void tableAssertions(AsynchronousConnectionManager connectionManager, String table) throws SQLException, ClassNotFoundException {
		// Assert that the queue has an insert query
		Assert.assertTrue("CreateQueue doesn't have query", AsyncDBQueue.getInstance().getCreateQueue().size() == 1);

		// Assert that the queue has the correct table insert query
		CreateTableQuery createTableQuery = AsyncDBQueue.getInstance().getCreateQueue().peek();
		Assert.assertTrue("CreateQueue contains wrong table", createTableQuery.getTable().equals(table));

		// Since this is just a test, write the db queue on the same thread
		AsyncDBQueue.getInstance().writeCreateTableQueue(connectionManager.openConnection());

		// Verify the table is created, if the query fails then the table does not exist
		connectionManager.openConnection().createStatement().execute("SELECT 1 FROM contributions LIMIT 1");
	}
}
