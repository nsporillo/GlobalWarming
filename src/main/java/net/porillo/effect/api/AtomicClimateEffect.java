package net.porillo.effect.api;

import net.porillo.effect.api.change.EffectChange;

import java.util.Collection;
import java.util.function.Supplier;

public abstract class AtomicClimateEffect<Data, Return extends EffectChange> extends ClimateEffect {
    // TODO: Do even more useful stuff
    public abstract Supplier<? extends Collection<Return>> execute(Data data);

}
