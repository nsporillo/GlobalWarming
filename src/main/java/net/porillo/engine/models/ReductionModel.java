package net.porillo.engine.models;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Model;
import org.bukkit.Material;

import java.util.Map;

public class ReductionModel extends Model {

    @Getter private Map<Material, Double> reductionMap;

    public ReductionModel(String worldName) {
        super(worldName, "reductionModel.json");
        this.loadModel();
    }

    @Override
    public void loadModel() {
        this.reductionMap = ClimateEngine.getInstance().getGson().fromJson(
                super.getContents(),
                new TypeToken<Map<Material, Double>>() {
                }.getType());

        if (this.reductionMap == null) {
            throw new RuntimeException(String.format("No values found in: [%s]", super.getPath()));
        }
    }

    public double getReduction(Material block) {
        if (reductionMap.containsKey(block)) {
            return reductionMap.get(block);
        } else {
            GlobalWarming.getInstance().getLogger().warning(String.format("No reduction defined in the model for: [%s]", block.name()));
            return 0;
        }
    }
}
