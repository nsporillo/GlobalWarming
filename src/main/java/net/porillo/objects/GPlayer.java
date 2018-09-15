package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.porillo.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GPlayer {

	/**
	 * Unique ID in database
	 */
	private Integer uniqueId;
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
	private Integer carbonScore;

	public GPlayer(ResultSet rs) throws SQLException {
		this.uniqueId = rs.getInt(1);
		this.uuid = UUID.fromString(rs.getString(2));
		this.firstSeen = rs.getLong(3);
		this.carbonScore = rs.getInt(4);
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public void sendMsg(String msg) {
		getPlayer().sendMessage(msg);
	}

	public void sendMsg(Lang lang) {
		sendMsg(lang.get());
	}

	public void sendMsg(Lang lang, Object... args) {
		sendMsg(lang.get(args));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		GPlayer gPlayer = (GPlayer) o;

		return uniqueId.equals(gPlayer.uniqueId);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + uniqueId.hashCode();
		return result;
	}
}
