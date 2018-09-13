package net.porillo.effect.negative.formation;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.porillo.GlobalWarming;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ListenerClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFormEvent;

@ClimateData(type = ClimateEffectType.ICE_FORMATION)
public class IceForm extends ListenerClimateEffect {

    private Distribution heightMap;

    @EventHandler
    public void blockFormEvent(BlockFormEvent event) {
        if (event.getNewState().getType() == Material.ICE) {
            double temp = ClimateEngine.getInstance().getClimateEngine(event.getBlock().getWorld().getName()).getTemperature();

            if (event.getBlock().getY() < heightMap.getValue(temp)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        this.heightMap = GlobalWarming.getInstance().getGson().fromJson(jsonModel, new TypeToken<Distribution>(){}.getType());
        if (this.heightMap == null) {
            unregister();
        }
    }

}
