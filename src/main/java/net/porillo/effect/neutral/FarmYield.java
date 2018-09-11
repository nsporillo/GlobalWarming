package net.porillo.effect.neutral;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ListenerClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.WorldClimateEngine;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.HashMap;
import java.util.Random;

@ClimateData(type = ClimateEffectType.FARM_YIELD)
public class FarmYield extends ListenerClimateEffect {

    private Gson gson;
    private HashMap<Material, Distribution> cropDistribution;
    private ClimateEngine climateEngine;

    public FarmYield() {
        super();
        this.gson = new Gson();
        this.climateEngine = ClimateEngine.getInstance();
    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent event) {
        Distribution distribution = cropDistribution.get(event.getBlock().getType());
        if (distribution != null) {
            WorldClimateEngine worldEngine = climateEngine.getClimateEngine(event.getBlock().getWorld().getName());
            double temp = worldEngine.getTemperature();
            double random = new Random().nextDouble();
            double chance = distribution.getValue(temp);

            if (chance / 100 <= random) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        this.cropDistribution = gson.fromJson(jsonModel, new TypeToken<HashMap<Material, Distribution>>(){}.getType());
        if (cropDistribution == null) {
            cropDistribution = new HashMap<>();
        }
    }


}
