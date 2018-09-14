package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Contribution;

import java.util.ArrayList;
import java.util.List;

public class ContributionTable extends Table {

	@Getter private final List<Contribution> contributions = new ArrayList<>();

	public ContributionTable() {
		super("contributions");
		createIfNotExists();
	}
}
