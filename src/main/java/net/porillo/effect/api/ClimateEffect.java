package net.porillo.effect.api;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public abstract class ClimateEffect {

	@Getter @Setter private String effectName;
	@Getter @Setter private Double effectThreshold;
	@Getter @Setter private JsonObject jsonModel;

}
