package net.porillo.effect.api.change.entity;

import net.porillo.effect.api.change.ChangeType;
import net.porillo.effect.api.change.EffectChange;
import org.bukkit.Location;

import java.util.UUID;

public class EntityChange implements EffectChange {

    private UUID entityUUID;
    private Location location;

    @Override
    public ChangeType getType() {
        return ChangeType.ENTITY;
    }
}
