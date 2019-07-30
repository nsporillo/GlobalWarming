package net.porillo.objects;

import lombok.*;
import org.bukkit.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackedBlock {

    private Integer uniqueId;
    private Integer ownerId;
    private Location location;
}
