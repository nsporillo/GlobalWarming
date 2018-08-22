package net.porillo.engine.models;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.Model;

public class EntityFitnessModel extends Model {

	private Map<EntityType, Distribution> entityFitnessMap = new HashMap<>();
	private Gson gson;
	
	public EntityFitnessModel() {
		super("entityFitnessModel.json");
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.loadModel();
	}
	
	// Useful for generating the JSON for all EntityTypes 
	// Uses a sample fitness distribution
	public void generateTestModel() throws IOException {
		for (EntityType entityType : EntityType.values()) {
			if (entityType.isAlive() && entityType.isSpawnable()) {
				entityFitnessMap.put(entityType, Distribution.sampleDistribution());
			}
		}
		
		super.writeContents(this.gson.toJson(entityFitnessMap));
	}

	@Override
	public void loadModel() {
		this.entityFitnessMap = this.gson.fromJson(super.getContents(), new TypeToken<Map<EntityType, Distribution>>(){}.getType());
	}
}
