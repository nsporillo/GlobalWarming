package net.porillo.database;

import net.porillo.database.queue.AsyncDBQueue;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class TableSelectTest {

	@Test
	public void testTableSelections() throws SQLException, ClassNotFoundException {
		Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();
		AsyncDBQueue.getInstance().writeSelectQueue(connection);
	}
}
