package net.porillo.engine.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.engine.api.Model;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ContributionModel extends Model {

	@Getter private Map<Material, Double> contributionMap;
	private Gson gson;

	public ContributionModel() {
		super("contributionModel.json");
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.loadModel();
	}

	@Override
	public void loadModel() {
		this.contributionMap = this.gson.fromJson(super.getContents(), new TypeToken<Map<Material, Double>>(){}.getType());
		
		if (this.contributionMap == null) {
			this.contributionMap = new HashMap<>();
			throw new RuntimeException("No values found in " + super.getName());
		}
	}
	
	public void generateTestModel() {
		Map<Material, Double> sampleMap = new HashMap<>();
		
		for (Material material : Material.values()) {
			if (material.isFuel() && !material.isLegacy()) {
				sampleMap.put(material, 2.0);
			}
		}
		
		super.writeContents(this.gson.toJson(sampleMap));
	}

	public double getContribution(Material fuelType) {
		if (contributionMap.containsKey(fuelType)) {
			return contributionMap.get(fuelType);
		} else {
			throw new NullPointerException("No contribution defined in the model for '" + fuelType.name() + "'");
		}
	}
	
	public static void main(String[] args) {
		new ContributionModel().generateTestModel();
	}
}
