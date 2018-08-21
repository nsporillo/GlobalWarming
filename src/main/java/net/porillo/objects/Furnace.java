package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.bukkit.Location;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Furnace {

	/**
	 * Random generated UUID for this furnace instance
	 */
	private UUID uniqueID;
	/**
	 * Associated GPlayer who placed this furnace
	 */
    private GPlayer owner;
    /**
     * The Bukkit location of this furnace
     */
    private Location location;

}
