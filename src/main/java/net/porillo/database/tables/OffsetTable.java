package net.porillo.database.tables;

import lombok.Data;
import lombok.Getter;
import net.porillo.objects.OffsetBounty;

import java.util.ArrayList;
import java.util.List;

public class OffsetTable extends Table {


	@Getter private List<OffsetBounty> offsetList = new ArrayList<>();

	public OffsetTable() {
		super("offsets");
	}

	@Override
	public void createIfNotExists() {

	}
}
