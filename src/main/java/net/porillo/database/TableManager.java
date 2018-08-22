package net.porillo.database;

import lombok.Getter;
import net.porillo.database.tables.*;

public class TableManager {

	@Getter private WorldTable worldTable;
	@Getter private PlayerTable playerTable;
	@Getter private ReductionTable reductionTable;
	@Getter private ContributionTable contributionTable;
	@Getter private FurnaceTable furnaceTable;
	@Getter private TreeTable treeTable;
	@Getter private OffsetTable offsetTable;

	public TableManager() {
		// TODO: Read table creation sql from file!
		this.worldTable = new WorldTable();
		this.playerTable = new PlayerTable();
		this.reductionTable = new ReductionTable();
		this.contributionTable = new ContributionTable();
		this.furnaceTable = new FurnaceTable();
		this.treeTable = new TreeTable();
		this.offsetTable = new OffsetTable();
	}
}
