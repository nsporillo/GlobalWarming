package net.porillo.effect.negative.formation;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ListenerClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.WorldClimateEngine;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFormEvent;

@ClimateData(type = ClimateEffectType.SNOW_FORMATION)
public class SnowForm extends ListenerClimateEffect {

    @Getter private Distribution heightMap;

    @EventHandler
    public void blockFormEvent(BlockFormEvent event) {
        if (event.getNewState().getType() == Material.SNOW) {
            WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(event.getBlock().getWorld().getUID());
            if (climateEngine != null && climateEngine.isEffectEnabled(ClimateEffectType.SNOW_FORMATION)) {
                double temperature = climateEngine.getTemperature();
                if (event.getBlock().getY() < heightMap.getValue(temperature)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        this.heightMap = GlobalWarming.getInstance().getGson().fromJson(jsonModel, new TypeToken<Distribution>() {
        }.getType());
        if (this.heightMap == null) {
            unregister();
        }
    }
}
