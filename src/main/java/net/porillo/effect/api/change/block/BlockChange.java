package net.porillo.effect.api.change.block;

import lombok.*;
import net.porillo.effect.api.change.EffectChange;
import net.porillo.effect.api.change.ChangeType;
import org.bukkit.Material;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BlockChange implements EffectChange {

	private final Material oldType;
	private final Material newType;
	private final int x,y,z;

	@Override
	public ChangeType getType() {
		return ChangeType.BLOCK;
	}
}
