package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.EntityType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class TrackedEntity {

    @Getter
    @Setter
    private Integer uniqueId;
    @Getter
    @Setter
    private UUID uuid;
    @Getter
    @Setter
    private Integer breederId;
    @Getter
    @Setter
    private EntityType entityType;
    @Getter
    @Setter
    private long ticksLived;
    @Getter
    @Setter
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
