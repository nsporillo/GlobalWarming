package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.OffsetBounty;

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

}
