package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
     * UUID of the furnace that corresponds to this emission
     * - Warning: furnaces may be deleted over time (this value may become invalid)
     */
    private Integer contributionKey;

    /**
     * UUID of the Bukkit world where the contribution took place
     */
    private UUID worldId;

    /**
     * Calculated emissions value for this contribution
     */
    private Integer contributionValue;

    public Contribution(ResultSet rs) throws SQLException {
        this.uniqueID = rs.getInt(1);
        this.contributer = rs.getInt(2);
        this.contributionKey = rs.getInt(3);
        this.worldId = UUID.fromString(rs.getString(4));
        this.contributionValue = rs.getInt(6);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Contribution that = (Contribution) o;
        return uniqueID.equals(that.uniqueID);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + uniqueID.hashCode();
        return result;
    }
}
