package net.porillo.commands;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.OffsetTable;
import net.porillo.objects.OffsetBounty;
import net.porillo.objects.Player;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BountyCommand extends BaseCommand {


	public BountyCommand(GlobalWarming plugin) {
		super(plugin);
		super.setName("bounty");
		super.addUsage("Starts a tree-planting bounty job.", "bountyId");
	}

	// TODO: When listing bounties, add a clickable chat link to easily start job
	// TODO: Add configurable player max concurrent bounties to prevent bounty hoarding
	@Override
	public void runCommand(CommandSender sender, List<String> args) {
		if (sender instanceof Player) {
			OffsetTable offsetTable = plugin.getTableManager().getOffsetTable();

			if (args.size() == 0) {
				int numBounties = offsetTable.getOffsetList().size();
				sender.sendMessage(ChatColor.YELLOW + "----- Showing " + numBounties + " Tree Planting Bounties");

				// TODO: Paginate if necessary
				for (OffsetBounty bounty : offsetTable.getOffsetList()) {
					if (bounty.isAvailable()) {
						bounty.showPlayerDetails(sender);
					}
				}
			} else {
				
			}
		}
	}
}
