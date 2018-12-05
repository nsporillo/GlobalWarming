package net.porillo.effect.neutral;

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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@ClimateData(type = ClimateEffectType.WEATHER)
public class Weather extends ListenerClimateEffect {

    public enum WeatherData {STORM, THUNDER, LIGHTNING, DURATION}

    private static int TICKS_PER_MINUTE = 1200;
    private static final int WEATHER_CHECK_INTERVAL = GlobalWarming.getInstance().getConf().getWeatherCheckInterval();
    @Getter public HashMap<WeatherData, Distribution> weatherDistribution;

    /**
     * Periodically create storms based on the current temperature
     */
    public Weather() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
              GlobalWarming.getInstance(),
              () -> {
                  for (World world : Bukkit.getWorlds()) {
                      //Storm:
                      WorldClimateEngine worldEngine = ClimateEngine.getInstance().getClimateEngine(world.getUID());
                      if (isAllowed(worldEngine, WeatherData.STORM)) {
                          world.setStorm(true);
                          int duration = (int) weatherDistribution.get(WeatherData.DURATION).getValue(worldEngine.getTemperature()) * TICKS_PER_MINUTE;
                          world.setWeatherDuration(duration);
                      }

                      //Thunder (if storming):
                      if (world.hasStorm() && isAllowed(worldEngine, WeatherData.THUNDER)) {
                          world.setThundering(true);
                      }

                      //Lightning strike (if storming):
                      // - Random player selected (must be outdoors)
                      if (world.hasStorm() && isAllowed(worldEngine, WeatherData.LIGHTNING)) {
                          Location location = getOutdoorPlayerLocation(world);
                          if (location != null) {
                              world.strikeLightning(location);
                          }
                      }
                  }
              }, 0L, WEATHER_CHECK_INTERVAL);
    }

    /**
     * Determine if an effect is allowed
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
     * Load the distribution model
     */
    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        this.weatherDistribution =
              GlobalWarming.getInstance().getGson().fromJson(
                    jsonModel,
                    new TypeToken<Map<WeatherData, Distribution>>() {
                    }.getType());

        if (this.weatherDistribution == null) {
            unregister();
        }
    }
}
