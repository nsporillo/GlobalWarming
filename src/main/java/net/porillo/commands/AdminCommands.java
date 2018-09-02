package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.porillo.effect.EffectEngine;
import net.porillo.objects.GPlayer;
import org.bukkit.ChatColor;

@CommandAlias("globalwarming|gw")
public class AdminCommands extends BaseCommand {

    @Subcommand("debug effect")
    @CommandPermission("globalwarming.admin.debug.effect")
    public class EffectCommands extends BaseCommand {

        @Subcommand("sealevel")
        @Syntax("[level]")
        @Description("Force execute a sealevel effect")
        public void onSeaLevel(GPlayer gPlayer, String[] args) {
            if (args.length < 2) {
                int seaLevel = 62;

                if (args.length == 1) {
                    try {
                        seaLevel = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        gPlayer.sendMsg(ChatColor.RED + "Invalid SeaLevel");
                        return;
                    }
                }

                EffectEngine.getInstance().testSeaLevelRise(gPlayer.getPlayer().getLocation().getChunk(), seaLevel);
                gPlayer.sendMsg(ChatColor.GREEN + String.format("Applying sea level rise from y:%d to chunk", seaLevel));
            } else {
                gPlayer.sendMsg(ChatColor.RED + "Invalid Args");
            }
        }
    }

}
