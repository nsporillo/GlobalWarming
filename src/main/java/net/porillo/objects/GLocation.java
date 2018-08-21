package net.porillo.objects;

import lombok.*;
import org.bukkit.Location;

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class GLocation {

	private String worldName;
	private int x, y, z;

	public GLocation(Location loc) {
		this.worldName = loc.getWorld().getName();
		this.x = loc.getBlockX();
		this.y = loc.getBlockY();
		this.z = loc.getBlockZ();
	}
}
