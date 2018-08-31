package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reduction {

	/**
	 * Unique Id in DB created for this contribution
	 */
	private Long uniqueID;
	/**
	 * Unique Id  of the Player who caused this redution
	 */
	private Long reductioner;
	/**
	 * Unique Id  of the associated object that corresponds to this reduction
	 */
	private Long reductionKey;
	/**
	 * Name of the Bukkit world this contribution took place
	 */
	private String worldName;
	/**
	 * Calculated emissions reduction value
	 */
	private double reductionValue;
}
