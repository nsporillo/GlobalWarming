package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.porillo.effect.EffectEngine;
import net.porillo.objects.GPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("globalwarming|gw")
@CommandPermission("globalwarming.admin")
public class DebugCommand extends BaseCommand {

    @Subcommand("debug effect")
    @Syntax("[effect]")
    @Description("Force execute a certain effect")
    public void onEffect(GPlayer gPlayer, String[] args) {
        Player player = Bukkit.getPlayer(gPlayer.getUuid());
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Please specify a effect");
        } else {
            String effect = args[0];
            if (effect.equalsIgnoreCase("sealevel")) {
                int seaLevel = 62;

                if (args.length == 2) {
                    try {
                        seaLevel = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid Sea Level");
                        return;
                    }
                }

                EffectEngine.getInstance().testSeaLevelRise(player.getLocation().getChunk(), seaLevel);
                player.sendMessage(ChatColor.GREEN + String.format("Applying sea level rise from y=%d to chunk", seaLevel));
            }
        }
    }



}
