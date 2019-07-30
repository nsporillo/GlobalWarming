package net.porillo.effect;

import net.porillo.effect.api.ClimateEffectType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClimateData {

    ClimateEffectType type();

    boolean provideModel() default true;

}
