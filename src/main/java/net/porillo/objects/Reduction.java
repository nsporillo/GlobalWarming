package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reduction {

	/**
	 * Unique Id in DB created for this contribution
	 */
	private Integer uniqueID;
	/**
	 * Unique Id  of the Player who caused this redution
	 */
	private Integer reductioner;
	/**
	 * Unique Id  of the associated object that corresponds to this reduction
	 */
	private Integer reductionKey;
	/**
	 * Name of the Bukkit world this contribution took place
	 */
	private String worldName;
	/**
	 * Calculated emissions reduction value
	 */
	private Integer reductionValue;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Reduction reduction = (Reduction) o;

		return uniqueID.equals(reduction.uniqueID);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + uniqueID.hashCode();
		return result;
	}
}
