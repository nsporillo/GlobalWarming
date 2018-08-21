package net.porillo.engine;

import org.apache.commons.lang.math.NumberUtils;

import net.porillo.GlobalWarming;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContributionModel extends Model {

	private Map<String, Double> contributionMap = new HashMap<>();

	public ContributionModel() {
		super("contributionModel");
		this.loadModel();
	}

	@Override
	public void loadModel() {
		try {
			BufferedReader reader = super.getReader();
			String line;

			while ((line = reader.readLine()) != null) {
				String[] args = line.split(":");
				if (NumberUtils.isNumber(args[1])) {
					contributionMap.put(args[0], Double.parseDouble(args[1]));
				} else {
					//TODO handle
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
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
