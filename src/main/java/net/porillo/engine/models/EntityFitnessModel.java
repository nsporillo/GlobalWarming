package net.porillo.engine.models;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.Model;
import org.bukkit.entity.EntityType;

import java.util.Map;

public class EntityFitnessModel extends Model {

	@Getter private Map<EntityType, Distribution> entityFitnessMap;

	public EntityFitnessModel(String worldName) {
		super(worldName,"entityFitnessModel.json");
		this.loadModel();
	}
	
	@Override
	public void loadModel() {
		this.entityFitnessMap = ClimateEngine.getInstance().getGson()
				.fromJson(super.getContents(), new TypeToken<Map<EntityType, Distribution>>(){}.getType());

		if (this.entityFitnessMap == null) {
			throw new RuntimeException("No values found in " + super.getPath());
		}
	}
}
