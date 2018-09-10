package net.porillo.effect;

import net.porillo.effect.api.ClimateEffectType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface ClimateData {

    ClimateEffectType type();

}
