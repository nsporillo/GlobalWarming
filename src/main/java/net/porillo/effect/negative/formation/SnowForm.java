package net.porillo.effect.negative.formation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.porillo.effect.api.ListenerClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.util.MapUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFormEvent;

import java.util.TreeMap;

public class SnowForm extends ListenerClimateEffect {

    private TreeMap<Double, Integer> heightMap;

    @EventHandler
    public void blockFormEvent(BlockFormEvent event) {
        if (event.getBlock().getType() == Material.ICE) {
            double temp = ClimateEngine.getInstance().getClimateEngine(event.getBlock().getWorld().getName()).getTemperature();

            if (event.getBlock().getY() < MapUtil.searchTreeMap(heightMap, temp)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        this.heightMap = new Gson().fromJson(jsonModel, new TypeToken<TreeMap<Double, Integer>>(){}.getType());
        if (this.heightMap == null) {
            unregister();
        }
    }

}
