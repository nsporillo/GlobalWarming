package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contribution {

	/**
	 * Random UUID created for this contribution
	 */
	private Integer uniqueID;
	/**
	 * UUID of the Player who caused this contribution
	 */
	private Integer contributer;
	/**
	 * UUID of the associated object that corresponds to this emission
	 */
	private Integer contributionKey;
	/**
	 * Name of the Bukkit world this contribution took place
	 */
	private String worldName;
	/**
	 * Calculated emissions value for this contribution
	 */
	private Integer contributionValue;

}
