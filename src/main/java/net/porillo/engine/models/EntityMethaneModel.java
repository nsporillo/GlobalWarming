package net.porillo.engine.models;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Model;
import org.bukkit.entity.EntityType;

import java.util.Map;

public class EntityMethaneModel extends Model {

    @Getter
    private Map<EntityType, Double> entityMethaneMap;

    public EntityMethaneModel(String worldName) {
        super(worldName, "entityMethaneModel.json");
        this.loadModel();
    }

    @Override
    public void loadModel() {
        try {
            this.entityMethaneMap = ClimateEngine.getInstance().getGson().fromJson(
                    super.getContents(),
                    new TypeToken<Map<EntityType, Double>>() {
                    }.getType());
        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
            GlobalWarming.getInstance().getLogger().severe("Error loading model file: " + super.getPath());
            GlobalWarming.getInstance().getLogger().severe("Could not load into the expected <EntityType, MobDistribution> mapping.");
            GlobalWarming.getInstance().getLogger().severe("Please check the formatting and verify the types are correct.");
        }

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
