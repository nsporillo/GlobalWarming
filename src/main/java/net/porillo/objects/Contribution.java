package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contribution {

	private UUID uniqueID;
	private UUID contributer;
	private UUID contributionKey;
	private String worldName;
	private double contributionValue;

}
