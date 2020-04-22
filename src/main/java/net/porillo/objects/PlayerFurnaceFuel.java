package net.porillo.objects;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;

import java.util.UUID;

@Data
@Builder
public class PlayerFurnaceFuel {

    private UUID playerId;
    private Material fuelType;
    private int count;

}
