package net.porillo.config;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.effect.api.ClimateEffectType;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorldConfig extends ConfigLoader {

    @Getter private final UUID worldId;
    @Getter private boolean enabled;
    @Getter private Set<ClimateEffectType> enabledEffects;
    @Getter private UUID associatedWorldId;

    public WorldConfig(UUID worldId) {
        super(String.format("%s.yml", Bukkit.getWorld(worldId).getName()), "world.yml");
        super.saveIfNotExist();
        this.worldId = worldId;
        super.load();
    }

    @Override
    protected void loadKeys() {
        this.enabled = this.conf.getBoolean("enabled");
        this.associatedWorldId = Bukkit.getWorld(this.conf.getString("association")).getUID();
        this.enabledEffects = new HashSet<>();

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
            worldName = world.getName();
        }

        return worldName;
    }
}
