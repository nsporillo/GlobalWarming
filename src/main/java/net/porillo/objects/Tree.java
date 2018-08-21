package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.bukkit.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tree {

	private UUID uniqueID;
	private GPlayer owner;
	private Location location;
	private int size;

}
