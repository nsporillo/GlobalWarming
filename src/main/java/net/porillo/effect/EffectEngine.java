package net.porillo.effect;

import com.google.gson.JsonObject;
import net.porillo.effect.api.ClimateEffect;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.negative.SeaLevelRise;

import java.util.*;

public class EffectEngine {

	private static EffectEngine effectEngine;

	private HashMap<ClimateEffectType, ClimateEffect> effects = new HashMap<>();
	private HashMap<ClimateEffectType, Class<? extends ClimateEffect>> effectClasses = new HashMap<>();
	private EffectModel model;
	private double minTemp;

	public EffectEngine() {
		registerClass(SeaLevelRise.class);

		this.model = new EffectModel();

		loadEffects();
	}

	private void loadEffects() {
		for (Map.Entry<ClimateEffectType, Class<? extends ClimateEffect>> entry : effectClasses.entrySet()) {
			if (model.isEnabled(entry.getKey())) {
				JsonObject data = model.getEffect(entry.getKey());

				try {
					ClimateEffect instance = entry.getValue().newInstance();
					effects.put(entry.getKey(), instance);
					instance.setJsonModel(data.getAsJsonObject("temp"));
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void registerClass(Class<? extends ClimateEffect> clazz) {
		ClimateData climateData = clazz.getDeclaredAnnotation(ClimateData.class);
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
