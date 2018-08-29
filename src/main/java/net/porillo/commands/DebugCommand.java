package net.porillo.commands;

import net.porillo.GlobalWarming;
import net.porillo.effects.EffectEngine;
import net.porillo.effects.SeaLevelRise;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DebugCommand extends BaseCommand {

	DebugCommand(GlobalWarming plugin) {
		super(plugin);
		super.setName("debug");
		super.addUsage("Debug things");
		super.setPermission("globalwarming.debug");
	}

	@Override
	public void runCommand(CommandSender sender, List<String> args) {
		if (args.size() > 1) {
			if (args.get(0).equals("effects")) {
				if (args.get(1).equals("sealevel")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						World world = player.getWorld();
						SeaLevelRise seaLevelRise = new SeaLevelRise(world, player.getLocation());
						EffectEngine.getInstance().processChunk(world, seaLevelRise);
						sender.sendMessage(ChatColor.GREEN + String.format("Applying sea level rise to chunk"));
					}
				} else {
					// Catch all command error handling
					sender.sendMessage(ChatColor.RED + "Did you mean /gw debug effects sealevel");
				}
			}
		} else {
			sender.sendMessage("/gw debug effects sealevel");
		}
	}
}
