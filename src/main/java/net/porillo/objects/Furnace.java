package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.porillo.GlobalWarming;
import net.porillo.database.tables.PlayerTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Furnace {

	/**
	 * Random generated UUID for this furnace instance
	 */
	private Integer uniqueID;
	/**
	 * Associated GPlayer who placed this furnace
	 */
    private Integer ownerID;
    /**
     * The Bukkit location of this furnace
     */
    private Location location;

	/**
	 * If this furnace currently exists in the world
	 */
	private boolean active;

	public Furnace(ResultSet rs) throws SQLException {
		this.uniqueID = rs.getInt(1);
		this.ownerID = rs.getInt(2);
		this.location = Bukkit.getWorld(rs.getString(3))
				.getBlockAt(rs.getInt(4), rs.getInt(5), rs.getInt(6)).getLocation();
		this.active = rs.getBoolean(7);
	}

	public GPlayer getOwner() {
		PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
		UUID ownerUUID = playerTable.getUuidMap().get(uniqueID);
		return playerTable.getPlayers().get(ownerUUID);
	}
}
