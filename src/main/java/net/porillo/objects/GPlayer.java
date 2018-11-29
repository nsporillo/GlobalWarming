package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.porillo.config.Lang;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    /**
     * Track each player's current world
     * - This information is needed when players go offline, but have planted saplings,
     * left furnaces running or have active bounties
     */
    private UUID worldId;

    public GPlayer(ResultSet rs) throws SQLException {
        this.uniqueId = rs.getInt(1);
        this.uuid = UUID.fromString(rs.getString(2));
        this.firstSeen = rs.getLong(3);
        this.carbonScore = rs.getInt(4);
        this.worldId = UUID.fromString(rs.getString(5));
    }

    /**
     * @return player-record if online, NULL otherwise
     */
    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * @return never NULL, even when player-record does not exist
     */
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public UUID getAssociatedWorldId() {
        UUID associatedWorldId = null;
        WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(getWorldId());
        if (climateEngine != null) {
            associatedWorldId = climateEngine.getConfig().getAssociatedWorldId();
        }

        return associatedWorldId;
    }

    public void sendMsg(String msg) {
        Player onlinePlayer = getOnlinePlayer();
        if (onlinePlayer != null) {
            onlinePlayer.sendMessage(msg);
        }
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
