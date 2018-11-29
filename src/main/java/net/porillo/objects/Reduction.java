package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reduction {

    /**
     * Unique Id in DB created for this reduction
     */
    private Integer uniqueID;

    /**
     * Unique Id of the Player who caused this reduction
     */
    private Integer reductioner;

    /**
     * Unique Id of the associated object that corresponds to this reduction
     * - Warning: furnaces may be deleted over time (this value may become invalid)
     */
    private Integer reductionKey;

    /**
     * UUID of the Bukkit world where the reduction took place
     */
    private UUID worldId;

    /**
     * Calculated emissions reduction value
     */
    private Integer reductionValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Reduction reduction = (Reduction) o;
        return uniqueID.equals(reduction.uniqueID);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + uniqueID.hashCode();
        return result;
    }
}
