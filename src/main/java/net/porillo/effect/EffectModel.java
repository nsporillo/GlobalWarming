package net.porillo.effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.engine.api.Model;

import java.util.HashMap;
import java.util.Map;

public class EffectModel extends Model {

    @Getter private Map<ClimateEffectType, JsonObject> effectMap;

    public EffectModel() {
        super("","effectModel.json");
        this.loadModel();
    }

    @Override
    public void loadModel() {
        this.effectMap = GlobalWarming.getInstance().getGson()
                .fromJson(super.getContents(), new TypeToken<Map<ClimateEffectType, JsonObject>>(){}.getType());

        if (this.effectMap == null) {
            throw new RuntimeException("No values found in " + super.getPath());
        }
    }

    public boolean isEnabled(ClimateEffectType effectType) {
        JsonObject effect = effectMap.get(effectType);
        if (effect == null) {
            return false;
        } else {
            JsonPrimitive enabled = effect.getAsJsonPrimitive("enabled");
            if (enabled.isBoolean()) {
                return enabled.getAsBoolean();
            } else {
                return false;
            }
        }
    }

    public JsonObject getEffect(ClimateEffectType effectType) {
        if (isEnabled(effectType)) {
            return effectMap.get(effectType);
        } else {
            return new JsonObject();
        }
    }

    public HashMap<ClimateEffectType, JsonObject> getEffects() {
        HashMap<ClimateEffectType, JsonObject> effects = new HashMap<>();
        for (Map.Entry<ClimateEffectType, JsonObject> entry : effectMap.entrySet()) {
            if (isEnabled(entry.getKey())) {
                effects.put(entry.getKey(), entry.getValue());
            }
        }
        return effects;
    }

}
