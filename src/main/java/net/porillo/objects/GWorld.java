package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GWorld {

	/**
	 * Unique ID in database
	 */
	private Long uniqueID;
	/**
	 * Bukkit worldname
	 */
	private String worldName;
	/**
	 * Temperature value, derived directly from the scoreTempModel
	 */
	private double temperature;
	/**
	 * When we loaded this world into GlobalWarming.
	 * This might be useful one day to normalize the rate of temperature
	 * change so that worlds don't implode if a lot of CO2 is emitted. 
	 * 
	 * In reality, changes take some time to be realized in the world. 
	 */
	private long firstSeen;
	/**
	 * Number of chunks that have ever been loaded in this world
	 * We increment on ChunkPopulateEvent, which only happens once
	 * for every chunk. 
	 * 
	 * We want to incorporate size into the equation somehow, since
	 * a tiny world with lots of furnaces and no trees should feel
	 * the impact.
	 */
	private int size;
	/**
	 * Numerical value representing the total amount of carbon 
	 * in the worlds atmosphere. Initially 0
	 */
	private int carbonValue;

	/**
	 * The y coordinate which represents the current world sea level
	 * Changes based on the effects of climate change
	 */
	private int seaLevel;
}
