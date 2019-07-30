package net.porillo.database;

import lombok.Getter;
import net.porillo.database.tables.*;

import java.util.Arrays;
import java.util.List;

@Getter
public class TableManager {

    private static TableManager instance;

    // Tables which load data from DB into memory
    private WorldTable worldTable;
    private PlayerTable playerTable;
    private FurnaceTable furnaceTable;
    private TreeTable treeTable;
    private OffsetTable offsetTable;
    private EntityTable entityTable;

    // Tables that currently don't load any data from the DB
    private ReductionTable reductionTable;
    private ContributionTable contributionTable;

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
