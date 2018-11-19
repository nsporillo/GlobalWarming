package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

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
	private Integer creatorId;
	/**
	 * The player who is fulfilling this carbon offset bounty
	 * Null if the bounty is available to be picked up.
	 */
	// TODO: Consider allowing multiple players to participate
	// in someone's bounty, and the reward be split evenly
	private Integer hunterId;

	/**
	 * World this offset bounty must be completed in
	 */
	private String worldName;
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

	public OffsetBounty(ResultSet rs)  throws SQLException {
		this.uniqueId = rs.getInt(1);
		this.creatorId = rs.getInt(2);
		this.hunterId = rs.getInt(3);
		this.worldName = rs.getString(4);
		this.logBlocksTarget = rs.getInt(5);
		this.reward = rs.getInt(6);
		this.timeStarted = rs.getLong(7);
		this.timeCompleted = rs.getLong(8);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		OffsetBounty bounty = (OffsetBounty) o;

		return uniqueId.equals(bounty.uniqueId);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + uniqueId.hashCode();
		return result;
	}
}
