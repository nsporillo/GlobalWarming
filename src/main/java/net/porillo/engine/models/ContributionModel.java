package net.porillo.engine.models;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.porillo.GlobalWarming;
import net.porillo.engine.api.Model;

public class ContributionModel extends Model {

	private Map<Material, Double> contributionMap;
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
			GlobalWarming.getInstance().getLogger().warning("Did not find any values in " + super.getName());
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
			GlobalWarming.getInstance().getLogger().severe("No contribution defined in the model for '" + fuelType.name() + "'");
			return 0;
		}
	}
	
	public static void main(String[] args) {
		new ContributionModel().generateTestModel();
	}
}
