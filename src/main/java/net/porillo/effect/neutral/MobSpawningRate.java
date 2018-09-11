package net.porillo.effect.neutral;

import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ListenerClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.WorldClimateEngine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Random;

@ClimateData(type = ClimateEffectType.MOB_SPAWN_RATE, provideModel = false)
public class MobSpawningRate extends ListenerClimateEffect {

    private ClimateEngine climateEngine;

    public MobSpawningRate() {
        super();
        this.climateEngine = ClimateEngine.getInstance();
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event) {
        WorldClimateEngine worldEngine = climateEngine.getClimateEngine(event.getLocation().getWorld().getName());
        Distribution distribution = worldEngine.getEntityFitnessModel().getEntityFitnessMap().get(event.getEntityType());
        if (distribution != null) {
            double temp = worldEngine.getTemperature();
            double chance = distribution.getValue(temp);
            double random = new Random().nextDouble();

            if (chance / 100 <= random) {
                event.setCancelled(true);
            }
        }
    }

}
