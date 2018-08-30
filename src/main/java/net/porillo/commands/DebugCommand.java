package net.porillo.commands;

import net.porillo.GlobalWarming;
import net.porillo.effect.EffectEngine;
import org.bukkit.ChatColor;
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
			if (args.get(0).equals("effect")) {
				if (args.get(1).equals("sealevel")) {
					if (sender instanceof Player) {
						int seaLevel = 62;

						if (args.size() > 2) {
							seaLevel = Integer.parseInt(args.get(2));
						}

						Player player = (Player) sender;
						EffectEngine.getInstance().testSeaLevelRise(player.getLocation().getChunk(), seaLevel);
						sender.sendMessage(ChatColor.GREEN + String.format("Applying sea level rise from y=%d to chunk", seaLevel));
					}
				} else {
					// Catch all command error handling
					sender.sendMessage(ChatColor.RED + "Did you mean /gw debug effect sealevel");
				}
			}
		} else {
			sender.sendMessage("/gw debug effect sealevel");
		}
	}
}
