package net.porillo.effects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Material;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BlockChange {

	private final Material oldType;
	private final Material newType;
	private final int x,y,z;
}
