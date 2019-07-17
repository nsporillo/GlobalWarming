package net.porillo.database.queue;

import lombok.Getter;
import lombok.Setter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.*;
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

	@Getter @Setter private boolean isDemo = false; // if no MySQL, just complain and skip storing anything.

	@Getter private Queue<CreateTableQuery> createQueue = new ConcurrentLinkedQueue<>();
	@Getter private Queue<InsertQuery> insertQueue = new ConcurrentLinkedQueue<>();
	@Getter private ConcurrentHashQueue<UpdateQuery<?>> updateQueue = new ConcurrentHashQueue<>();
	@Getter private Queue<DeleteQuery> deleteQueue = new ConcurrentLinkedQueue<>();
	@Getter private Queue<SelectQuery<?,?>> selectQueue = new ConcurrentLinkedQueue<>();

	@Getter @Setter private boolean debug;

	private BukkitRunnable queueWriteThread = new BukkitRunnable() {
		@Override
		public void run() {
			try {
				if (isSyncNeeded()) {
					GlobalWarming.getInstance().getLogger().info("Syncing database...");
					writeQueues();
					GlobalWarming.getInstance().getLogger().info("Finished syncing database.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void scheduleAsyncTask(long interval) {
		queueWriteThread.runTaskTimerAsynchronously(GlobalWarming.getInstance(), 0L, interval);
	}

	public void close() {
		if (!isDemo) {
			queueWriteThread.run();
		}
	}

	public void runQueueWriteTaskNow() {
		queueWriteThread.runTaskAsynchronously(GlobalWarming.getInstance());
	}

	public void queueSelectQuery(SelectQuery selectQuery) {
		this.selectQueue.offer(selectQuery);
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

	private void writeQueues() throws Exception {
		Connection connection = GlobalWarming.getInstance().getConnectionManager().openConnection();
		writeCreateTableQueue(connection);
		writeInsertQueue(connection);
		writeUpdateQueue(connection);
		writeDeleteQueue(connection);
		writeSelectQueue(connection);
	}

	public void writeSelectQueue(Connection connection){
		for (SelectQuery<?,?> obj = selectQueue.poll(); obj != null; obj = selectQueue.poll()) {
			try {
				// Executes query, transforms ResultSet into List of objects, and calls back
				obj.execute(connection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void executeStatement(Query query, Connection connection) {
		try {
			PreparedStatement statement = query.prepareStatement(connection);
			if (debug) GlobalWarming.getInstance().getLogger().info(statement.toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void writeDeleteQueue(Connection connection){
		for (DeleteQuery deleteQuery = deleteQueue.poll(); deleteQuery != null; deleteQuery = deleteQueue.poll()) {
			executeStatement(deleteQuery, connection);
		}
	}

	public void writeInsertQueue(Connection connection) {
		for (InsertQuery insertQuery = insertQueue.poll(); insertQuery != null; insertQuery = insertQueue.poll()) {
			executeStatement(insertQuery, connection);
		}
	}

	public void writeCreateTableQueue(Connection connection) {
		for (CreateTableQuery tableQuery = createQueue.poll(); tableQuery != null; tableQuery = createQueue.poll()) {
			executeStatement(tableQuery, connection);
		}
	}

	public void writeUpdateQueue(Connection connection) {
		for (UpdateQuery updateQuery = updateQueue.poll(); updateQuery != null; updateQuery = updateQueue.poll()) {
			executeStatement(updateQuery, connection);
		}
	}

	public boolean isSyncNeeded() {
		return !createQueue.isEmpty() || !insertQueue.isEmpty()
				|| !updateQueue.isEmpty() || !deleteQueue.isEmpty() || !selectQueue.isEmpty();
	}

	public static AsyncDBQueue getInstance() {
		if (instance == null) {
			instance = new AsyncDBQueue();
		}

		return instance;
	}
}
