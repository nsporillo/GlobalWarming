package net.porillo.effect.api;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.porillo.effect.ClimateData;
import net.porillo.effect.EffectEngine;

@AllArgsConstructor
public abstract class ClimateEffect {

	@Getter
	@Setter private JsonObject jsonModel;
	private ClimateData climateData;

	public ClimateEffect() {
		this.climateData = getClass().getAnnotation(ClimateData.class);
	}

	public String getName() {
		if (climateData != null) {
			return climateData.type().name();
		}
		return "";
	}

	public ClimateEffectType getType() {
		if (climateData != null) {
			return climateData.type();
		}
		return ClimateEffectType.NONE;
	}

	public void unregister() {
		EffectEngine.getInstance().unregisterEffect(getType());
	}

}
