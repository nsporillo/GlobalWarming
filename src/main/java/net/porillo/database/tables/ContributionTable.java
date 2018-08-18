package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Contribution;
import net.porillo.objects.Reduction;

import java.util.ArrayList;
import java.util.List;

public class ContributionTable extends Table {

	@Getter  private List<Contribution> reductions = new ArrayList<>();

	public ContributionTable() {
		super("contributions");
	}

	@Override
	public void createIfNotExists() {

	}

	@Override
	public List<Contribution> loadTable() {
		return null;
	}
}
