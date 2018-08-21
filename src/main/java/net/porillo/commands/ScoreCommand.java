package net.porillo.commands;

import net.porillo.GlobalWarming;
import net.porillo.objects.GPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreCommand extends BaseCommand {

	public ScoreCommand(GlobalWarming plugin) {
		super(plugin);
		super.setName("score");
		super.addUsage("View your carbon footprint scorecard");
	}

	@Override
	public void runCommand(CommandSender sender, List<String> args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Map<UUID, GPlayer> playerMap = plugin.getTableManager().getPlayerTable().getPlayers();

			// If player is not in the memory yet, their score is 0.
			int carbonScore = playerMap.containsKey(player.getUniqueId()) ? playerMap.get(player.getUniqueId()).getCarbonScore(): 0;
			player.sendMessage("Your current carbon footprint is " + formatScore(carbonScore));
			player.sendMessage("Your goal is to keep your score as close to zero as possible!");
		}
	}

	// TODO: Make configurable
	// TODO: Add more colors
	private String formatScore(int score) {
		if (score <= 0) {
			return ChatColor.GREEN + String.valueOf(score);
		} else if (score <= 500) {
			return ChatColor.YELLOW + String.valueOf(score);
		} else {
			return ChatColor.RED + String.valueOf(score);
		}
	}
}
