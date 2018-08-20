package net.porillo.database;

import lombok.Getter;
import net.porillo.database.tables.*;

public class TableManager {

	@Getter private WorldTable worldTable;
	@Getter private PlayerTable playerTable;
	@Getter private ReductionTable reductionTable;
	@Getter private ContributionTable contributionTable;
	@Getter private FurnaceTable furnaceTable;
	@Getter private PlayerFurnaceTable playerFurnaceTable;

	public TableManager() {
		this.worldTable = new WorldTable();
		this.playerTable = new PlayerTable();
		this.reductionTable = new ReductionTable();
		this.contributionTable = new ContributionTable();
		this.furnaceTable = new FurnaceTable();
		this.playerFurnaceTable = new PlayerFurnaceTable();
	}
}
