package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.select.Selection;
import net.porillo.database.api.select.SelectionResult;
import net.porillo.objects.OffsetBounty;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OffsetTable extends Table {

	/**
	 * In memory storage of all available OffsetBounty
	 * When an offset bounty is complete, delete from this list
	 * On startup, query the offset table for available OffsetBounty's
	 */
	@Getter private List<OffsetBounty> offsetList = new ArrayList<>();

	public OffsetTable() {
		super("offsets");
		createIfNotExists();
	}

	@Override
	public Selection makeSelectionQuery() {
		String sql = "SELECT * FROM offsets WHERE hunter IS NULL;";
		return new Selection(getTableName(), sql);
	}

	@Override
	public void onResultArrival(SelectionResult result) throws SQLException {
		List<OffsetBounty> offsetBounties = new ArrayList<>();
		ResultSet rs = result.getResultSet();

		while (rs.next()) {
			offsetBounties.add(new OffsetBounty(rs));
		}

		if (result.getTableName().equals(getTableName())) {
			new BukkitRunnable() {

				@Override
				public void run() {
					offsetList.addAll(offsetBounties);
				}
			}.runTask(GlobalWarming.getInstance());
		}
	}
}
