package net.porillo.config;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.effect.api.ClimateEffectType;

import java.util.HashSet;
import java.util.Set;

public class WorldConfig extends ConfigLoader {

	@Getter private final String world;

	@Getter private boolean enabled;
	@Getter private Set<ClimateEffectType> enabledEffects;
	@Getter private String association;

	public WorldConfig( String world) {
		super(world + ".yml");
		super.saveIfNotExist();
		this.world = world;
		super.load();
	}

	@Override
	protected void loadKeys() {
		this.enabled = this.conf.getBoolean("enabled");
		this.association = this.conf.getString("association");
		this.enabledEffects = new HashSet<>();

		for (String effect : this.conf.getStringList("enabledEffects")) {
			try {
				this.enabledEffects.add(ClimateEffectType.valueOf(effect));
			} catch(IllegalArgumentException ex) {
				GlobalWarming.getInstance().getLogger().severe(String.format("Could not load effect %s for [%s]", effect, world));
			}
		}
	}

	@Override
	protected void reload() {
		this.enabledEffects.clear();
		super.rereadFromDisk();
		super.load();
	}
}
