package net.porillo.util;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.commands.GeneralCommands;
import net.porillo.config.Lang;
import net.porillo.effect.EffectEngine;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.negative.SeaLevelRise;
import net.porillo.effect.negative.formation.IceForm;
import net.porillo.effect.negative.formation.SnowForm;
import net.porillo.effect.neutral.FarmYield;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.WorldClimateEngine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintain one periodic notification-message per world (uses the scoreboard's worlds)
 *  - One message per player
 *  - Messages are based on each world's temperature
 *  - Supports effect-model modifications
 */
public class CO2Notifications {
    @Getter
    private Map<String, BossBar> bossBars;
    private static final long NOTIFICATION_INTERVAL_TICKS = 6000; //5 minutes
    private static final long NOTIFICATION_DURATION_TICKS = 300; //15 seconds
    private static final int NORMAL_SEA_LEVEL = 63;
    private static final double NORMAL_ICE_LEVEL_HEIGHT = 0.0;
    private static final double NORMAL_SNOW_LEVEL_HEIGHT = 0.0;
    private static final double NORMAL_FARM_YIELD_FITNESS = 100.0;
    private static final double NORMAL_MOB_FITNESS = 100.0;
    private enum TemperatureRange {LOW, AVERAGE, HIGH}

    public CO2Notifications() {
        bossBars = new HashMap<>();
        showPlayerNotifications();
    }

    private void showPlayerNotifications() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
              GlobalWarming.getInstance(),
              () -> {
                  //Create notification for all players, grouped by associated-world
                  synchronized (this) {
                      bossBars.clear();
                      GScoreboard scoreboard = GlobalWarming.getInstance().getScoreboard();
                      for (String worldName : scoreboard.getScoreboards().keySet()) {
                          World world = Bukkit.getWorld(worldName);
                          if (world != null) {
                              BossBar bossBar = Bukkit.createBossBar(
                                    getNotificationMessage(worldName),
                                    BarColor.WHITE,
                                    BarStyle.SOLID);

                              bossBars.put(worldName, bossBar);
                              for (Player player : world.getPlayers()) {
                                  bossBar.addPlayer(player);
                              }
                          }
                      }
                  }

                  //Hide the notification after some time:
                  Bukkit.getScheduler().scheduleAsyncDelayedTask(
                        GlobalWarming.getInstance(),
                        () -> {
                            synchronized (this) {
                                for (BossBar bossBar : bossBars.values()) {
                                    bossBar.removeAll();
                                }
                            }
                        },
                        NOTIFICATION_DURATION_TICKS);
              }, 0L, NOTIFICATION_INTERVAL_TICKS);
    }

    private String getNotificationMessage(String worldName) {
        String message = Lang.ENGINE_DISABLED.get();
        WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(worldName);
        if (climateEngine != null && climateEngine.isEnabled()) {
            double temperature = climateEngine.getTemperature();
            message = getTemperatureGuidance(worldName, temperature);
        }

        return message;
    }

    private String getTemperatureGuidance(String worldName, double temperature) {
        //Get the temperature range:
        String message = "";
        String optional = "";
        TemperatureRange range;
        if (temperature < GeneralCommands.LOW_TEMPERATURE_UBOUND) {
            range = TemperatureRange.LOW;
        } else if (temperature < GeneralCommands.HIGH_TEMPERATURE_LBOUND) {
            range = TemperatureRange.AVERAGE;
        } else {
            range = TemperatureRange.HIGH;
        }

        try {
            //Get a message based on current conditions:
            String index = String.valueOf((int) temperature);
            double random = GlobalWarming.getInstance().getRandom().nextDouble();
            WorldClimateEngine worldClimateEngine = ClimateEngine.getInstance().getClimateEngine(worldName);
            if (worldClimateEngine != null) {
                if (worldClimateEngine.isEffectEnabled(ClimateEffectType.FARM_YIELD) && random < 0.1) {
                    //Farm yields (with random materials):
                    FarmYield farmYield = EffectEngine.getInstance().getEffect(FarmYield.class, ClimateEffectType.FARM_YIELD);
                    List<Material> keys = new ArrayList<>(farmYield.getCropDistribution().keySet());
                    Material randomMaterial = keys.get(GlobalWarming.getInstance().getRandom().nextInt(keys.size()));
                    Distribution distribution = farmYield.getCropDistribution().get(randomMaterial);
                    double farmYieldFitness = distribution.getValue(temperature);
                    optional = randomMaterial.toString().toLowerCase().replace("_", "");
                    message = getMessage(
                          farmYieldFitness < NORMAL_FARM_YIELD_FITNESS,
                          range,
                          Lang.NOTIFICATION_FARM_LOW,
                          Lang.NOTIFICATION_FARM_OK,
                          Lang.NOTIFICATION_FARM_HIGH);
                } else if (worldClimateEngine.isEffectEnabled(ClimateEffectType.ICE_FORMATION) && random < 0.2) {
                    //Ice:
                    Distribution distribution = EffectEngine.getInstance().getEffect(IceForm.class, ClimateEffectType.ICE_FORMATION).getHeightMap();
                    double iceFitness = distribution.getValue(temperature);
                    message = getMessage(
                          iceFitness != NORMAL_ICE_LEVEL_HEIGHT,
                          range,
                          Lang.NOTIFICATION_ICE_LOW,
                          Lang.NOTIFICATION_ICE_OK,
                          Lang.NOTIFICATION_ICE_HIGH);
                } else if (worldClimateEngine.isEffectEnabled(ClimateEffectType.MOB_SPAWN_RATE) && random < 0.3) {
                    //Mob (with random entities):
                    List<EntityType> keys = new ArrayList<>(worldClimateEngine.getEntityFitnessModel().getEntityFitnessMap().keySet());
                    EntityType randomEntity = keys.get(GlobalWarming.getInstance().getRandom().nextInt(keys.size()));
                    Distribution distribution = worldClimateEngine.getEntityFitnessModel().getEntityFitnessMap().get(randomEntity);
                    double mobFitness = distribution.getValue(temperature);
                    optional = randomEntity.toString().toLowerCase().replace("_", "");
                    message = getMessage(
                          mobFitness < NORMAL_MOB_FITNESS,
                          range,
                          Lang.NOTIFICATION_MOB_LOW,
                          Lang.NOTIFICATION_MOB_OK,
                          Lang.NOTIFICATION_MOB_HIGH);
                } else if (worldClimateEngine.isEffectEnabled(ClimateEffectType.SEA_LEVEL_RISE) && random < 0.4) {
                    //Sea-level messages:
                    SeaLevelRise seaLevelRise = EffectEngine.getInstance().getEffect(SeaLevelRise.class, ClimateEffectType.SEA_LEVEL_RISE);
                    int seaLevelFitness = seaLevelRise.getJsonModel().get(index).getAsInt();
                    message = getMessage(
                          seaLevelFitness != NORMAL_SEA_LEVEL,
                          range,
                          Lang.NOTIFICATION_SEALEVEL_LOW,
                          Lang.NOTIFICATION_SEALEVEL_OK,
                          Lang.NOTIFICATION_SEALEVEL_HIGH);
                } else if (worldClimateEngine.isEffectEnabled(ClimateEffectType.SNOW_FORMATION) && random < 0.5) {
                    //Snow messages:
                    Distribution distribution = EffectEngine.getInstance().getEffect(SnowForm.class, ClimateEffectType.SNOW_FORMATION).getHeightMap();
                    double snowFitness = distribution.getValue(temperature);
                    message = getMessage(
                          snowFitness != NORMAL_SNOW_LEVEL_HEIGHT,
                          range,
                          Lang.NOTIFICATION_SNOW_LOW,
                          Lang.NOTIFICATION_SNOW_OK,
                          Lang.NOTIFICATION_SNOW_HIGH);
                }
            }
        } finally {
            //Default messages:
            if (message.isEmpty()) {
                message = getMessage(
                      range != TemperatureRange.AVERAGE,
                      range,
                      Lang.NOTIFICATION_DEFAULT_LOW,
                      Lang.NOTIFICATION_DEFAULT_OK,
                      Lang.NOTIFICATION_DEFAULT_HIGH);
            }
        }

        return String.format(
              message,
              GeneralCommands.getTemperatureColor(temperature),
              optional);
    }

    private static String getMessage(boolean isEffectActive, TemperatureRange range, Lang tooLow, Lang ok, Lang tooHigh) {
        String message = ok.get();
        if (isEffectActive) {
            switch (range) {
                case LOW:
                    message = tooLow.get();
                    break;
                case HIGH:
                    message = tooHigh.get();
                    break;
            }
        }

        return message;
    }
}