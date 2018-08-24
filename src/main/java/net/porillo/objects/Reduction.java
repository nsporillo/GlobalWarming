package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reduction {

	/**
	 * Random UUID created for this contribution
	 */
	private UUID uniqueID;
	/**
	 * UUID of the Player who caused this redution
	 */
	private UUID reductioner;
	/**
	 * UUID of the associated object that corresponds to this reduction
	 */
	private UUID reductionKey;
	/**
	 * Name of the Bukkit world this contribution took place
	 */
	private String worldName;
	/**
	 * Calculated emissions reduction value
	 */
	private double reductionValue;
}
