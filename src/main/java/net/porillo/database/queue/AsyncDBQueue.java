package net.porillo.database.queue;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.DeleteQuery;
import net.porillo.database.api.InsertQuery;
import net.porillo.database.api.UpdateQuery;
import net.porillo.database.queries.other.CreateTableQuery;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Singleton class designed to facilitate thread-safe
 * asynchronous database operations for GlobalWarming.
 *
 * We maintain an in-memory storage of all the data we
 * need during the operation of the plugin. For all
 * in-memory storage operations, we want an associated
 * update/insert/delete to the corresponding DB table.
 *
 * We want to first and foremost ensure all DB operations
 * are on NOT done on the main thread, as it will hang the
 * server for a short period of time. This is not acceptable
 * for production servers, where Ticks Per Second (TPS) is
 * mission critical for players and server owners.
 *
 * Additionally, we can queue up updates and execute them in
 * batch at regular intervals, and on plugin shutdown. Wherever
 * possible, we want to batch similar api for performance.
 *
 * Note: we also want to load *some* contents of the database
 * into memory on plugin startup. However, it will not be done
 * using this Queue since we want to do that immediately.
 *
 */
public class AsyncDBQueue {

	private static AsyncDBQueue instance;

	/**
	 * Each query type has it's own queue
	 * Allows for simplistic query batching
	 */
	@Getter private Queue<DeleteQuery> deleteQueue = new ConcurrentLinkedQueue<>();
	@Getter private Queue<InsertQuery> insertQueue = new ConcurrentLinkedQueue<>();
	@Getter private Queue<CreateTableQuery> createQueue = new ConcurrentLinkedQueue<>();
	@Getter private Queue<UpdateQuery> updateQueue = new ConcurrentLinkedQueue<>();

	private BukkitRunnable queueWriteThread = new BukkitRunnable() {

		@Override
		public void run() {
			try {
				GlobalWarming.getInstance().getLogger().info("Syncing database...");

				if (!createQueue.isEmpty()) {
					GlobalWarming.getInstance().getLogger().info(String.format("Executing %d %s", createQueue.size(), "table creates."));
				}
				if (!insertQueue.isEmpty()) {
					GlobalWarming.getInstance().getLogger().info(String.format("Executing %d %s", insertQueue.size(), "inserts."));
				}
				if (!updateQueue.isEmpty()) {
					GlobalWarming.getInstance().getLogger().info(String.format("Executing %d %s", updateQueue.size(), "updates."));
				}
				if (!deleteQueue.isEmpty()) {
					GlobalWarming.getInstance().getLogger().info(String.format("Executing %d %s", deleteQueue.size(), "deletes."));
				}

				writeQueues();
				GlobalWarming.getInstance().getLogger().info("Finished syncing database.");
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	};

	public void scheduleAsyncTask(long interval) {
		queueWriteThread.runTaskTimerAsynchronously(GlobalWarming.getInstance(), 100L, interval);
	}

	public void close() {
		queueWriteThread.run();
	}

	public void runQueueWriteTaskNow() {
		queueWriteThread.runTaskAsynchronously(GlobalWarming.getInstance());
	}

	public void queueDeleteQuery(DeleteQuery deleteQuery) {
		this.deleteQueue.offer(deleteQuery);
	}

	public void queueCreateQuery(CreateTableQuery createTableQuery) {
		this.createQueue.offer(createTableQuery);
	}

	public void queueInsertQuery(InsertQuery insertQuery) {
		this.insertQueue.offer(insertQuery);
	}

	public void queueUpdateQuery(UpdateQuery updateQuery) {
		this.updateQueue.offer(updateQuery);
	}

	private void writeQueues() throws SQLException, ClassNotFoundException {
		Connection connection = GlobalWarming.getInstance().getConnectionManager().openConnection();
		writeCreateTableQueue(connection);
		writeInsertQueue(connection);
		writeUpdateQueue(connection);
		writeDeleteQueue(connection);

		connection.close(); // close connection after all queues written
	}

	public void writeDeleteQueue(Connection connection){
		for (DeleteQuery obj = deleteQueue.poll(); obj != null; obj = deleteQueue.poll()) {
			try {
				PreparedStatement statement = obj.prepareStatement(connection);
				statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeInsertQueue(Connection connection) {
		for (InsertQuery obj = insertQueue.poll(); obj != null; obj = insertQueue.poll()) {
			try {
				PreparedStatement statement = obj.prepareStatement(connection);
				statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeCreateTableQueue(Connection connection) {
		for (CreateTableQuery obj = createQueue.poll(); obj != null; obj = createQueue.poll()) {
			try {
				PreparedStatement statement = obj.prepareStatement(connection);
				statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeUpdateQueue(Connection connection) {
		for (UpdateQuery obj = updateQueue.poll(); obj != null; obj = updateQueue.poll()) {
			try {
				PreparedStatement statement = obj.prepareStatement(connection);
				statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static AsyncDBQueue getInstance() {
		if (instance == null) {
			instance = new AsyncDBQueue();
		}

		return instance;
	}
}
