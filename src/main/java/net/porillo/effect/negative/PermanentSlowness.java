package net.porillo.effect.negative;

import com.google.gson.JsonObject;
import lombok.Getter;
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

import java.util.UUID;

@ClimateData(type = ClimateEffectType.PERMANENT_SLOWNESS)
public class PermanentSlowness extends ScheduleClimateEffect implements Listener {

    private int duration;
    @Getter private double temperatureThreshold;

    private void updatePlayerSlowness(Player player, double temperature) {
        if (temperature >= temperatureThreshold) {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, duration, 1);
            player.addPotionEffect(potionEffect);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID worldId = event.getPlayer().getWorld().getUID();
        WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(worldId);
        if (climateEngine != null && climateEngine.isEffectEnabled(ClimateEffectType.PERMANENT_SLOWNESS)) {
            updatePlayerSlowness(event.getPlayer(), climateEngine.getTemperature());
        }
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(world.getUID());
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
        setPeriod(jsonModel.get("interval").getAsInt());
        duration = jsonModel.get("duration").getAsInt();
        temperatureThreshold = jsonModel.get("threshold").getAsDouble();
    }
}
