package net.porillo.engine.models;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.MobDistribution;
import net.porillo.engine.api.Model;
import org.bukkit.entity.EntityType;

import java.util.Map;

public class EntityFitnessModel extends Model {

    @Getter private Map<EntityType, MobDistribution> entityFitnessMap;

    public EntityFitnessModel(String worldName) {
        super(worldName, "entityFitnessModel.json");
        this.loadModel();
    }

    @Override
    public void loadModel() {
        try {
            this.entityFitnessMap = GlobalWarming.getInstance().getGson().fromJson(
                    super.getContents(),
                    new TypeToken<Map<EntityType, MobDistribution>>() {
                    }.getType());
        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
            GlobalWarming.getInstance().getLogger().severe("Error loading model file: " + super.getPath());
            GlobalWarming.getInstance().getLogger().severe("Could not load into the expected <EntityType, MobDistribution> mapping.");
            GlobalWarming.getInstance().getLogger().severe("Please check the formatting and verify the types are correct.");
            return;
        }

        if (this.entityFitnessMap == null) {
            throw new RuntimeException(String.format("No values found in: [%s]", super.getPath()));
        }
    }
}
