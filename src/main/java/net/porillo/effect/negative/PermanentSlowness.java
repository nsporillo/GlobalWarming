package net.porillo.effect.negative;

import com.google.gson.JsonObject;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ScheduleClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@ClimateData(type = ClimateEffectType.PERMANENT_SLOWNESS)
public class PermanentSlowness extends ScheduleClimateEffect implements Listener {

    private double tempThreshold;

    public PermanentSlowness() {
        this.setPeriod(5 * 60 * 20);
    }

    private void updatePlayerSlowness(Player player, double temperature) {
        if (tempThreshold < temperature) {
            int potionDuration = 6 * 60 * 20;
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, potionDuration, 1);
            player.addPotionEffect(potionEffect);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String eventWorldName = event.getPlayer().getWorld().getName();
        WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(eventWorldName);
        if (climateEngine != null && climateEngine.isEffectEnabled(ClimateEffectType.PERMANENT_SLOWNESS)) {
            updatePlayerSlowness(event.getPlayer(), climateEngine.getTemperature());
        }
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(world.getName());
            if (climateEngine != null && climateEngine.isEffectEnabled(ClimateEffectType.PERMANENT_SLOWNESS)) {
                for (Player player : world.getPlayers()) {
                    updatePlayerSlowness(player, climateEngine.getTemperature());
                }
            }
        }
    }


    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        tempThreshold = jsonModel.get("threshold").getAsDouble();
    }
}
