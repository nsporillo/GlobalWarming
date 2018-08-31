package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Contribution;

import java.util.ArrayList;
import java.util.List;

public class ContributionTable extends Table {

	@Getter private List<Contribution> reductions = new ArrayList<>();

	public ContributionTable() {
		super("contributions");
		createIfNotExists();
	}

	public List<Contribution> loadTable() {
		return null;
	}
}
