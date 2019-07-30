package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@NoArgsConstructor
@AllArgsConstructor
public class TrackedBlock {
    @Getter
    @Setter
    private Integer uniqueId;
    @Getter
    @Setter
    private Integer ownerId;
    @Getter
    @Setter
    private Location location;
}
