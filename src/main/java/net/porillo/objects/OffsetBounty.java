package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffsetBounty {

	private Player creator;
	private Player hunter;
	private int treeTarget;
	private double reward;
	private long timeStarted, timeCompleted;

	public boolean isAvailable() {
		return hunter == null;
	}

	public void showPlayerDetails(CommandSender sender) {
		// TODO: Make bounty links clickable

	}
}
