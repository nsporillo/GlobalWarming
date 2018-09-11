package net.porillo.effect.negative;

import com.google.gson.JsonObject;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ScheduleClimateEffect;
import net.porillo.engine.ClimateEngine;
import org.bukkit.Bukkit;
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

    private void updatePlayerSlowness(Player player) {
        double temp = ClimateEngine.getInstance().getClimateEngine(player.getWorld().getName()).getTemperature();
        if (tempThreshold < temp) {
            int potionDuration = 6 * 60 * 20;
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, potionDuration, 1);
            player.addPotionEffect(potionEffect);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerSlowness(event.getPlayer());
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerSlowness(player);
        }
    }


    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        tempThreshold = jsonModel.get("threshold").getAsDouble();
    }

}
