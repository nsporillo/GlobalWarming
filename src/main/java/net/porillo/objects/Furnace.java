package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Furnace {

	private UUID uniqueID;
    private GPlayer owner;
    private GLocation location;

}
