package net.porillo.database;

import lombok.Getter;
import net.porillo.database.tables.ContributionTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.database.tables.ReductionTable;
import net.porillo.database.tables.WorldTable;

public class TableManager {

	@Getter private WorldTable worldTable;
	@Getter private PlayerTable playerTable;
	@Getter private ReductionTable reductionTable;
	@Getter private ContributionTable contributionTable;

	public TableManager() {
		this.worldTable = new WorldTable();
		this.playerTable = new PlayerTable();
		this.reductionTable = new ReductionTable();
		this.contributionTable = new ContributionTable();
	}
}
