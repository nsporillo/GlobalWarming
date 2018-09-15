package net.porillo.database;

import lombok.Getter;
import net.porillo.database.tables.*;

public class TableManager {

	private static TableManager instance;

	// Tables which load data from DB into memory
	@Getter private WorldTable worldTable;
	@Getter private PlayerTable playerTable;
	@Getter private FurnaceTable furnaceTable;
	@Getter private TreeTable treeTable;
	@Getter private OffsetTable offsetTable;

	// Tables that currently dont load any data from the DB
	@Getter private ReductionTable reductionTable;
	@Getter private ContributionTable contributionTable;

	public TableManager() {
		this.worldTable = new WorldTable();
		this.playerTable = new PlayerTable();
		this.furnaceTable = new FurnaceTable();
		this.treeTable = new TreeTable();
		this.offsetTable = new OffsetTable();

		this.reductionTable = new ReductionTable();
		this.contributionTable = new ContributionTable();
	}

	public static TableManager getInstance() {
		if (instance == null) {
			instance = new TableManager();
		}

		return instance;
	}
}
