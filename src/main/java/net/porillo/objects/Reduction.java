package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reduction {

	private UUID uniqueID;
	private UUID reductioner;
	private String worldName;
	private double reductionValue;
}
