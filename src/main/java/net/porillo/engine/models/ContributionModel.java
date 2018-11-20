package net.porillo.engine.models;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Model;
import org.bukkit.Material;

import java.util.Map;

public class ContributionModel extends Model {

	@Getter private Map<Material, Double> contributionMap;

	public ContributionModel(String worldName) {
		super(worldName, "contributionModel.json");
		this.loadModel();
	}

	@Override
	public void loadModel() {
		this.contributionMap = ClimateEngine.getInstance().getGson()
				.fromJson(super.getContents(), new TypeToken<Map<Material, Double>>() {}.getType());

		if (this.contributionMap == null) {
			throw new RuntimeException(String.format("No values found in: [%s]", super.getPath()));
		}
	}

	public double getContribution(Material fuelType) {
		if (contributionMap.containsKey(fuelType)) {
			return contributionMap.get(fuelType);
		} else {
			throw new NullPointerException(String.format("No contribution defined in the model for: [%s]", fuelType.name()));
		}
	}
}
