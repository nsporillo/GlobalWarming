package net.porillo.engine.models;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Model;
import org.bukkit.entity.EntityType;

import java.util.Map;

public class EntityMethaneModel extends Model {

    @Getter private Map<EntityType, Double> entityMethaneMap;

    public EntityMethaneModel(String worldName) {
        super(worldName, "entityMethaneModel.json");
        this.loadModel();
    }

    @Override
    public void loadModel() {
        this.entityMethaneMap = ClimateEngine.getInstance().getGson().fromJson(
              super.getContents(),
              new TypeToken<Map<EntityType, Double>>() {
              }.getType());

        if (this.entityMethaneMap == null) {
            throw new RuntimeException(String.format("No values found in: [%s]", super.getPath()));
        }
    }

    public double getContribution(EntityType entityType) {
        if (entityMethaneMap.containsKey(entityType)) {
            return entityMethaneMap.get(entityType);
        } else {
            GlobalWarming.getInstance().getLogger().info(String.format("No contribution defined in the model for: [%s]", entityType.name()));
            return 0.0;
        }
    }
}
