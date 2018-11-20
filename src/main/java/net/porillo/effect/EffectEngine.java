package net.porillo.effect;

import com.google.gson.JsonObject;
import net.porillo.GlobalWarming;
import net.porillo.effect.api.ClimateEffect;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ListenerClimateEffect;
import net.porillo.effect.api.ScheduleClimateEffect;
import net.porillo.effect.negative.PermanentSlowness;
import net.porillo.effect.negative.SeaLevelRise;
import net.porillo.effect.negative.formation.IceForm;
import net.porillo.effect.negative.formation.SnowForm;
import net.porillo.effect.neutral.FarmYield;
import net.porillo.effect.neutral.MobSpawningRate;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class EffectEngine {

	private static EffectEngine effectEngine;

	private HashMap<ClimateEffectType, ClimateEffect> effects = new HashMap<>();
	private HashMap<ClimateEffectType, Class<? extends ClimateEffect>> effectClasses = new HashMap<>();
	private EffectModel model;

	private EffectEngine() {
		registerClass(SeaLevelRise.class);
		registerClass(MobSpawningRate.class);
		registerClass(FarmYield.class);
		registerClass(SnowForm.class);
		registerClass(IceForm.class);
		registerClass(PermanentSlowness.class);

		this.model = new EffectModel();

		loadEffects();
	}

	private void loadEffects() {
		for (Map.Entry<ClimateEffectType, Class<? extends ClimateEffect>> entry : effectClasses.entrySet()) {
			if (model.isEnabled(entry.getKey())) {
				JsonObject data = model.getEffect(entry.getKey());

				ClimateEffect effect;
				try {
					effect = entry.getValue().getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
					continue;
				}

				effects.put(entry.getKey(), effect);
				if (entry.getValue().getAnnotation(ClimateData.class).provideModel()) {
					effect.setJsonModel(data.getAsJsonObject("model"));
				}

				if (effect instanceof Listener) {
					Bukkit.getPluginManager().registerEvents((Listener) effect, GlobalWarming.getInstance());
				}

				if (effect instanceof ScheduleClimateEffect) {
					ScheduleClimateEffect runnable = (ScheduleClimateEffect) effect;
					runnable.setTaskId(Bukkit.getScheduler().runTaskTimer(GlobalWarming.getInstance(), runnable, 0, runnable.getPeriod()).getTaskId());
				}
			}
		}
	}

	private void registerClass(Class<? extends ClimateEffect> clazz) {
		ClimateData climateData = clazz.getAnnotation(ClimateData.class);
		if (climateData != null) {
			effectClasses.put(climateData.type(), clazz);
		}
	}

	public void unregisterEffect(ClimateEffectType effectType) {
		ClimateEffect effect = effects.get(effectType);
		if (effect instanceof Listener) {
			HandlerList.unregisterAll((ListenerClimateEffect) effect);
		}
		if (effect instanceof ScheduleClimateEffect) {
			Bukkit.getScheduler().cancelTask(((ScheduleClimateEffect) effect).getTaskId());
		}

		effectClasses.remove(effectType);
		effects.remove(effectType);
	}

	public <T extends ClimateEffect> T getEffect(Class<T> clazz, ClimateEffectType effectType) {
		return clazz.cast(effects.get(effectType));
	}

	public static EffectEngine getInstance() {
		if (effectEngine == null) {
			effectEngine = new EffectEngine();
		}

		return effectEngine;
	}
}
