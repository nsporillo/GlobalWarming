package net.porillo.engine;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;

import com.google.gson.Gson;

import net.porillo.objects.Niche;

public class EntityFitnessModel extends Model {

	private Map<EntityType, Niche> entityNicheMap = new HashMap<>();
	
	private Gson gson;
	
	public EntityFitnessModel(String modelName) {
		super(modelName);
		this.gson = new Gson();
	}

	@Override
	public void loadModel() {
		
	}
	
	public void generateTestModel() {
		for (EntityType entityType : EntityType.values()) {
			if (entityType.isAlive() && entityType.isSpawnable()) {
				
			}
		}
	}

}
