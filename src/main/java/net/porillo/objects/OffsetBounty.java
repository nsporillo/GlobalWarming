package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffsetBounty {

	/**
	 * Unique integer ID of this offset bounty
	 */
	private Integer uniqueId;

	/**
	 * The player who created this carbon offset bounty
	 */
	private GPlayer creator;
	/**
	 * The player who is fulfilling this carbon offset bounty
	 * Null if the bounty is available to be picked up.
	 */
	// TODO: Consider allowing multiple players to participate
	// in someone's bounty, and the reward be split evenly
	private GPlayer hunter;

	/**
	 * World this offset bounty must be completed in
	 */
	private GWorld world;
	/**
	 * The required number of log blocks that need to be 
	 * grown by the hunter before this bounty is completed
	 */
	private Integer logBlocksTarget;
	/**
	 * The player defined reward for bounty completion
	 */
	private Integer reward;
	/**
	 * Variables to track time 
	 */
	private long timeStarted, timeCompleted;

	public boolean isAvailable() {
		return hunter == null;
	}

	public void showPlayerDetails(CommandSender sender) {
		// TODO: Make bounty links clickable

	}
}
