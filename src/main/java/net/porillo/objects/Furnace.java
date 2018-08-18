package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Furnace {

	private UUID uniqueID;
	private Player owner;
	private Location location;
	private boolean exists;

}
