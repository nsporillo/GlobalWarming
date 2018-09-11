package net.porillo.effect;

import com.google.gson.JsonObject;
import net.porillo.GlobalWarming;
import net.porillo.effect.api.ClimateEffect;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ListenerClimateEffect;
import net.porillo.effect.negative.SeaLevelRise;
import net.porillo.effect.neutral.FarmYield;
import net.porillo.effect.neutral.MobSpawningRate;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class EffectEngine {

	private static EffectEngine effectEngine;

	private HashMap<ClimateEffectType, ClimateEffect> effects = new HashMap<>();
	private HashMap<ClimateEffectType, Class<? extends ClimateEffect>> effectClasses = new HashMap<>();
	private EffectModel model;
	private double minTemp;

	private EffectEngine() {
		registerClass(SeaLevelRise.class);
		registerClass(MobSpawningRate.class);
		registerClass(FarmYield.class);

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
				if (effect instanceof ListenerClimateEffect) {
					Bukkit.getPluginManager().registerEvents((ListenerClimateEffect) effect, GlobalWarming.getInstance());
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
