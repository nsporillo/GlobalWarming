package net.porillo.engine.models;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import net.porillo.GlobalWarming;
import net.porillo.engine.api.Model;

public class ContributionModel extends Model {

	private Map<String, Double> contributionMap = new HashMap<>();

	public ContributionModel() {
		super("contributionModel.txt");
		this.loadModel();
	}

	@Override
	public void loadModel() {
		for (String line : super.getLines()) {
			String[] args = line.split(":");
			if (NumberUtils.isNumber(args[1])) {
				contributionMap.put(args[0], Double.parseDouble(args[1]));
			} else {
				// TODO handle non numbers in model file?
			}
		}
	}

	public double getContribution(String cause) {
		if (contributionMap.containsKey(cause)) {
			return contributionMap.get(cause);
		} else {
			GlobalWarming.getInstance().getLogger().severe("No contribution defined in the model for '" + cause + "'");
			return 0;
		}
	}
}
