package net.porillo.database;

import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.Table;
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

    @Test(priority = 2)
    public void testTableCreation() throws SQLException, ClassNotFoundException {
        Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();
        for (Table table : TableManager.getInstance().getTables()) {
            System.out.println("Testing table create for " + table.getTableName());
            table.createIfNotExists();
        }

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
            connection.createStatement().executeUpdate(String.format("DROP TABLE IF EXISTS %s", table));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tableAssertions(String table) throws SQLException, ClassNotFoundException {
        Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();

        // Verify the table is created, if the query fails then the table does not exist
        connection.createStatement().execute(String.format("SELECT 1 FROM %s LIMIT 1", table));
    }
}
