package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GPlayer {

	/**
	 * Unique ID in database
	 */
	private Long uniqueId;
	/**
	 * All players have a UUID assigned when they first join by CraftBukkit
	 */
	private UUID uuid;
	/**
	 * Log the first time this plugin has seen the player 
	 * CraftBukkit tracks the players first seen on the server, but 
	 * this plugin might be installed after that
	 */
	private long firstSeen;
	/**
	 * Numerical "carbon score" value for just this player
	 */
	private int carbonScore;


	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public void sendMsg(String msg) {
		getPlayer().sendMessage(msg);
	}

}
