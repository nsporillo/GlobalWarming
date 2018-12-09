package net.porillo.effect.neutral;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ScheduleClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.WorldClimateEngine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@ClimateData(type = ClimateEffectType.WEATHER)
public class Weather extends ScheduleClimateEffect {

    public enum WeatherData {STORM, THUNDER, STRIKE_PLAYER, DURATION}

    @Getter public HashMap<WeatherData, Distribution> weatherDistribution;

    /**
     * Determine if a weather effect is allowed
     */
    private boolean isAllowed(WorldClimateEngine worldEngine, WeatherData data) {
        boolean isAllowed = false;
        Distribution distribution = weatherDistribution.get(data);
        if (distribution != null && worldEngine != null && worldEngine.isEffectEnabled(ClimateEffectType.WEATHER)) {
            final double random = GlobalWarming.getInstance().getRandom().nextDouble();
            final double chance = distribution.getValue(worldEngine.getTemperature());
            isAllowed = random <= (chance / 100.f);
        }

        return isAllowed;
    }

    /**
     * Periodically create storms based on the current temperature
     */
    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            //Storm:
            WorldClimateEngine worldEngine = ClimateEngine.getInstance().getClimateEngine(world.getUID());
            if (isAllowed(worldEngine, WeatherData.STORM)) {
                world.setStorm(true);
                int duration = (int) weatherDistribution.get(WeatherData.DURATION).getValue(worldEngine.getTemperature());
                world.setWeatherDuration(duration);
            }

            //Thunder (if storming):
            if (world.hasStorm() && isAllowed(worldEngine, WeatherData.THUNDER)) {
                world.setThundering(true);
                int duration = (int) weatherDistribution.get(WeatherData.DURATION).getValue(worldEngine.getTemperature());
                world.setThunderDuration(duration);
            }

            //Lightning strike (if storming):
            // - Random player selected (must be outdoors)
            if (world.hasStorm() && isAllowed(worldEngine, WeatherData.STRIKE_PLAYER)) {
                Location location = getOutdoorPlayerLocation(world);
                if (location != null) {
                    world.strikeLightning(location);
                }
            }
        }
    }

    /**
     * Get a random player's location (must be outdoors, specifically: no block overhead)
     */
    private Location getOutdoorPlayerLocation(World world) {
        Location location = null;
        List<Player> players = world.getPlayers();
        if (players.size() > 0) {
            Player player = players.get(ThreadLocalRandom.current().nextInt(0, players.size()));
            if (world.getHighestBlockAt(player.getLocation()).getY() < player.getLocation().getY()) {
                location = player.getLocation();
            }
        }

        return location;
    }

    /**
     * Load the weather distribution model
     */
    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        this.weatherDistribution =
              GlobalWarming.getInstance().getGson().fromJson(
                    jsonModel.get("distribution"),
                    new TypeToken<Map<WeatherData, Distribution>>() {
                    }.getType());

        if (this.weatherDistribution == null) {
            unregister();
        } else {
            setPeriod(jsonModel.get("interval").getAsInt());
        }
    }
}
