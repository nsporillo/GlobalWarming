package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GWorld {

    /**
     * Unique ID in database
     */
    private Integer uniqueID;

    /**
     * Bukkit world ID
     */
    private UUID worldId;

    /**
     * When we loaded this world into GlobalWarming.
     * This might be useful one day to normalize the rate of temperature
     * change so that worlds don't implode if a lot of CO2 is emitted.
     * <p>
     * In reality, changes take some time to be realized in the world.
     */
    private long firstSeen;

    /**
     * Number of chunks that have ever been loaded in this world
     * We increment on ChunkPopulateEvent, which only happens once
     * for every chunk.
     * <p>
     * We want to incorporate size into the equation somehow, since
     * a tiny world with lots of furnaces and no trees should feel
     * the impact.
     */
    private Integer size;

    /**
     * Numerical value representing the total amount of carbon
     * in the worlds atmosphere. Initially 0
     */
    private Integer carbonValue;

    /**
     * The y coordinate which represents the current world sea level
     * Changes based on the effects of climate change
     */
    private Integer seaLevel;

    public GWorld(ResultSet rs) throws SQLException {
        this.uniqueID = rs.getInt(1);
        this.worldId = UUID.fromString(rs.getString(2));
        this.firstSeen = rs.getLong(3);
        this.carbonValue = rs.getInt(4);
        this.seaLevel = rs.getInt(5);
        this.size = rs.getInt(6);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GWorld gWorld = (GWorld) o;
        return uniqueID.equals(gWorld.uniqueID);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + uniqueID.hashCode();
        return result;
    }
}
