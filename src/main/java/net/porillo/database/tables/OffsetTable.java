package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.select.OffsetSelectQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.OffsetBounty;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OffsetTable extends Table implements SelectCallback<OffsetBounty> {

	/**
	 * In memory storage of all available OffsetBounty
	 * When an offset bounty is complete, delete from this list
	 * On startup, query the offset table for available OffsetBounty's
	 */
	@Getter private List<OffsetBounty> offsetList = new ArrayList<>();

	public OffsetTable() {
		super("offsets");
		createIfNotExists();

		OffsetSelectQuery selectQuery = new OffsetSelectQuery(this);
		AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
	}

	@Override
	public void onSelectionCompletion(List<OffsetBounty> returnList) throws SQLException {
		if (GlobalWarming.getInstance() != null) {
			new BukkitRunnable() {

				@Override
				public void run() {
					offsetList.addAll(returnList);
				}
			}.runTask(GlobalWarming.getInstance());
		} else {
			System.out.println("Selection returned " + returnList.size() + " offsets.");
		}
	}
}
