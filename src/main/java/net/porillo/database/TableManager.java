package net.porillo.database;

import lombok.Getter;
import net.porillo.database.tables.*;

import java.util.Arrays;
import java.util.List;

public class TableManager {

	private static TableManager instance;

	// Tables which load data from DB into memory
	@Getter private WorldTable worldTable;
	@Getter private PlayerTable playerTable;
	@Getter private FurnaceTable furnaceTable;
	@Getter private TreeTable treeTable;
	@Getter private OffsetTable offsetTable;
	@Getter private EntityTable entityTable;

	// Tables that currently don't load any data from the DB
	@Getter private ReductionTable reductionTable;
	@Getter private ContributionTable contributionTable;

	public TableManager() {
		this.worldTable = new WorldTable();
		this.playerTable = new PlayerTable();
		this.furnaceTable = new FurnaceTable();
		this.treeTable = new TreeTable();
		this.offsetTable = new OffsetTable();
		this.entityTable = new EntityTable();

		this.reductionTable = new ReductionTable();
		this.contributionTable = new ContributionTable();
	}

	public List<Table> getTables() {
		return Arrays.asList(worldTable, playerTable, furnaceTable, treeTable, offsetTable, entityTable);
	}

	public static TableManager getInstance() {
		if (instance == null) {
			instance = new TableManager();
		}

		return instance;
	}
}
