package net.porillo.effect.api;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.porillo.effect.api.change.EffectChange;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
public abstract class ClimateEffect<Data, Return extends EffectChange> {

	@Getter @Setter private String effectName;
	@Getter @Setter private Double effectThreshold;
	@Getter @Setter private JsonObject jsonModel;

	public abstract Collection<Return> execute(Data data);

}
