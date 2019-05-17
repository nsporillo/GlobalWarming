package net.porillo.config;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.effect.api.ClimateEffectType;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.porillo.engine.models.ScoreTempModel.CarbonSensitivity;

public class WorldConfig extends ConfigLoader {

    @Getter private final UUID worldId;
    @Getter private boolean enabled;
    @Getter private Set<ClimateEffectType> enabledEffects;
    @Getter private UUID associatedWorldId;
    @Getter private CarbonSensitivity sensitivity;
    @Getter private double blastFurnaceMultiplier;
    @Getter private double methaneTicksLivedModifier;
    @Getter private boolean bonemealReductionAllowed;
    @Getter private double bonemealReductionModifier;

    public WorldConfig(UUID worldId) {
        super(String.format("%s.yml", Bukkit.getWorld(worldId).getName()), "world.yml");
        super.saveIfNotExist();
        this.worldId = worldId;
        super.load();
    }

    public String getName() {
        return super.getFileName().substring(0, super.getFileName().indexOf("."));
    }

    @Override
    protected void loadKeys() {
        sensitivity = CarbonSensitivity.LOW;
        String carbonSensitivity = conf.getString("carbonSensitivity");
        if (carbonSensitivity != null && !carbonSensitivity.isEmpty()) {
            try {
                sensitivity = CarbonSensitivity.valueOf(carbonSensitivity);
            } catch (Exception e) {
                GlobalWarming.getInstance().getLogger().warning(
                      String.format(
                            "Unknown carbon sensitivity for: [%s], defaulting to [%s]",
                            getDisplayName(worldId),
                            sensitivity));
            }
        }

        this.enabled = this.conf.getBoolean("enabled");
        this.associatedWorldId = Bukkit.getWorld(this.conf.getString("association")).getUID();
        this.enabledEffects = new HashSet<>();
        this.blastFurnaceMultiplier = this.conf.getDouble("blastFurnaceMultiplier", 1.2);
        this.methaneTicksLivedModifier = this.conf.getDouble("methaneTicksLivedModifier", 0.01);
        this.bonemealReductionAllowed = this.conf.getBoolean("bonemealReductionAllowed", true);
        this.bonemealReductionModifier = this.conf.getDouble("bonemealReductionModifier", 0.5);

        for (String effect : this.conf.getStringList("enabledEffects")) {
            try {
                this.enabledEffects.add(ClimateEffectType.valueOf(effect));
            } catch (IllegalArgumentException ex) {
                GlobalWarming.getInstance().getLogger().severe(String.format(
                      "Could not load effect: [%s] for world: [%s]",
                      effect,
                      getDisplayName(worldId)));
            }
        }
    }

    @Override
    protected void reload() {
        this.enabledEffects.clear();
        super.reload();
    }

    public static String getDisplayName(UUID worldId) {
        String worldName = "UNKNOWN";
        if (worldId != null) {
            World world = Bukkit.getWorld(worldId);
            if (world != null) {
                worldName = world.getName();
            }
        }

        return worldName;
    }
}
