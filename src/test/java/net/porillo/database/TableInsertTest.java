package net.porillo.database;

import net.porillo.database.queries.insert.ContributionInsertQuery;
import net.porillo.database.queries.insert.PlayerInsertQuery;
import net.porillo.database.queries.insert.WorldInsertQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Contribution;
import net.porillo.objects.GPlayer;
import net.porillo.objects.GWorld;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

@Test
public class TableInsertTest {

    @Test
    public void testWorldInserts() throws Exception {
        Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();

        for (int i = 0; i < 30; i++) {
            GWorld gWorld = TestUtility.getInstance().nextRandomWorld();
            AsyncDBQueue.getInstance().queueInsertQuery(new WorldInsertQuery(gWorld));
        }

        AsyncDBQueue.getInstance().writeInsertQueue(connection);
    }

    @Test
    public void testPlayerInserts() throws Exception {
        Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();

        for (int i = 0; i < 300; i++) {
            GPlayer gPlayer = TestUtility.getInstance().nextRandomPlayer();
            AsyncDBQueue.getInstance().queueInsertQuery(new PlayerInsertQuery(gPlayer));
        }

        AsyncDBQueue.getInstance().writeInsertQueue(connection);
    }

    @Test
    public void testContributionTable() throws Exception {
        Connection connection = TestUtility.getInstance().getConnectionManager().openConnection();

        Random random = TestUtility.getInstance().getRandom();
        // Create a contribution and insert it into the DB
        final Integer uniqueId = random.nextInt(Integer.MAX_VALUE);
        UUID worldId = UUID.randomUUID();
        Contribution contribution =
              new Contribution(uniqueId, random.nextInt(Integer.MAX_VALUE), random.nextInt(Integer.MAX_VALUE), worldId, 15);
        AsyncDBQueue.getInstance().queueInsertQuery(new ContributionInsertQuery(contribution));
        AsyncDBQueue.getInstance().writeInsertQueue(connection);

        // Verify the object exists in the DB
        String select = "SELECT * FROM contributions WHERE uniqueId = ?";
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
            assertThat("worldId mismatch",
                  contribution.getWorldId().equals(UUID.fromString(resultSet.getString(4))));
            assertThat("value mismatch",
                  contribution.getContributionValue() == resultSet.getInt(5));
        }
    }

    // TODO: Add tests for all other tables
    // Warning, some object constructors require bukkit api methods
    // which can be cumbersome to work with in a testing environment
}
