package net.porillo.engine.models;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.AlternateDistribution;
import net.porillo.engine.api.Model;
import org.bukkit.entity.EntityType;

import java.util.Map;
import java.util.UUID;

public class EntityFitnessModel extends Model {

    @Getter private Map<EntityType, AlternateDistribution> entityFitnessMap;

    public EntityFitnessModel(UUID worldId) {
        super(worldId, "entityFitnessModel.json");
        this.loadModel();
    }

    @Override
    public void loadModel() {
        this.entityFitnessMap = ClimateEngine.getInstance().getGson().fromJson(
              super.getContents(),
              new TypeToken<Map<EntityType, AlternateDistribution>>() {
              }.getType());

        if (this.entityFitnessMap == null) {
            throw new RuntimeException(String.format("No values found in: [%s]", super.getPath()));
        }
    }
}
