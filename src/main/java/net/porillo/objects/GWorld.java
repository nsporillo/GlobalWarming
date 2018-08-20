package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GWorld {

	private String worldName;
	private double temperature;
	private long age;
	private int size, score;
}
