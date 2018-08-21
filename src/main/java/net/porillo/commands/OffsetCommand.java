package net.porillo.commands;

import net.porillo.GlobalWarming;
import net.porillo.database.tables.PlayerTable;
import net.porillo.objects.GPlayer;
import net.porillo.objects.OffsetBounty;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OffsetCommand extends BaseCommand {

	public OffsetCommand(GlobalWarming plugin) {
		super(plugin);
		super.setName("offset");
		super.setRequiredArgs(2);
		super.addUsage("Set tree-planting bounties to reduce carbon footprint", "logTarget", "reward");
	}

	@Override
	public void runCommand(CommandSender sender, List<String> args) {
		if (sender instanceof Player) {
			// Validate input
			Integer logTarget = null;
			Integer reward = null;

			try {
				logTarget = Integer.parseInt(args.get(0));
				reward = Integer.getInteger(args.get(1));

				if (logTarget <= 0 || reward <= 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException nfe) {
				sender.sendMessage("Error: <trees> and <reward> must be positive integers");
				return;
			}

			//TODO: Add economy integration
            Player player = (Player) sender;
			PlayerTable playerTable = plugin.getTableManager().getPlayerTable();

			if (playerTable.getPlayers().containsKey(player.getUniqueId())) {
				GPlayer creator = playerTable.getPlayers().get(player.getUniqueId());
				OffsetBounty bounty = new OffsetBounty();
				bounty.setCreator(creator);
				bounty.setLogBlocksTarget(logTarget);
				bounty.setReward(reward);
			} else {
				plugin.getLogger().severe("Error: Player " + sender.getName() + " tried to set a offset bounty"
						+ " but was not present in the player table.");
			}
		}
	}
}
