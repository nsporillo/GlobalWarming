package net.porillo.effect.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.porillo.effect.api.change.AtomicChange;

import java.util.HashSet;
import java.util.function.Supplier;

@NoArgsConstructor
@AllArgsConstructor
public abstract class ClimateEffect<Change extends AtomicChange> implements Supplier<HashSet<Change>> {

	/**
	 * ClimateEffects are meant to be serialized/deserialized from the worlds climate model file
	 * So sub class fields will be transient, we want only generalized information here
	 *
	 * Sub classes are instantiated for every chunk for instance, so each instance will have it's own data
	 */
	@Getter @Setter private String effectName;
	@Getter @Setter private Double effectThreshold;
}
