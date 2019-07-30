package net.porillo.effect.api.change.block;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.porillo.effect.api.change.ChangeType;
import net.porillo.effect.api.change.EffectChange;
import org.bukkit.Material;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BlockChange implements EffectChange {

    private final Material oldType;
    private final Material newType;
    private final int x, y, z;

    @Override
    public ChangeType getType() {
        return ChangeType.BLOCK;
    }
}
