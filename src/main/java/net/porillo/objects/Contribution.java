package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contribution {

	/**
	 * Random UUID created for this contribution
	 */
	private UUID uniqueID; 
	/**
	 * UUID of the Player who caused this contribution
	 */
	private UUID contributer; 
	/**
	 * UUID of the associated object that corresponds to this emission
	 */
	private UUID contributionKey;
	/**
	 * Name of the Bukkit world this contribution took place
	 */
	private String worldName;
	/**
	 * Calculated emissions value for this contribution
	 */
	private double contributionValue;

}
