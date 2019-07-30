package net.porillo.objects;

import lombok.*;
import org.bukkit.entity.EntityType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackedEntity {

    private Integer uniqueId;
    private UUID uuid;
    private Integer breederId;
    private EntityType entityType;
    private long ticksLived;
    private boolean alive;

    public TrackedEntity(ResultSet rs) throws SQLException {
        super();
        setUniqueId(rs.getInt(1));
        setUuid(UUID.fromString(rs.getString(2)));
        setBreederId(rs.getInt(3));
        setEntityType(EntityType.valueOf(rs.getString(4).toUpperCase()));
        setTicksLived(rs.getLong(5));
        setAlive(rs.getBoolean(6));
    }
}
