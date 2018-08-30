package net.porillo.effect.api.change.entity;

import net.porillo.effect.api.change.AtomicChange;
import net.porillo.effect.api.change.ChangeType;
import org.bukkit.Location;

import java.util.UUID;

public class EntityChange implements AtomicChange {

	private UUID entityUUID;
	private Location location;

	@Override
	public ChangeType getType() {
		return ChangeType.ENTITY;
	}
}
