package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.effect.EffectEngine;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.negative.SeaLevelRise;
import net.porillo.objects.GPlayer;
import org.bukkit.ChatColor;

@CommandAlias("globalwarming|gw")
public class AdminCommands extends BaseCommand {

    @Subcommand("debug")
    @CommandPermission("globalwarming.admin.debug")
    public class DebugCommands extends BaseCommand {

        @Subcommand("database|db")
        @Description("Toggles database console logging")
        public void onDatabaseDebug(GPlayer gPlayer, String[] args) {
            boolean value = AsyncDBQueue.getInstance().isDebug();
            AsyncDBQueue.getInstance().setDebug(!value);

            if (!value) {
                gPlayer.sendMsg(ChatColor.GREEN + "Database console logging = " +  ChatColor.YELLOW + "true.");
            } else {
                gPlayer.sendMsg(ChatColor.GREEN + "Database console logging = " + ChatColor.GRAY + "false.");
            }
        }

        @Subcommand("effect")
        @CommandPermission("globalwarming.admin.debug.effect")
        public class EffectCommands extends BaseCommand {

            @Subcommand("sealevel")
            @Syntax("[level]")
            @Description("Force execute Sea Level effect")
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

                    EffectEngine.getInstance().getEffect(SeaLevelRise.class, ClimateEffectType.SEA_LEVEL_RISE).execute(gPlayer.getPlayer().getLocation().getChunk().getChunkSnapshot(), seaLevel);
                    gPlayer.sendMsg(ChatColor.GREEN + String.format("Applying sea level rise from y:%d to chunk", seaLevel));
                } else {
                    gPlayer.sendMsg(ChatColor.RED + "Invalid Args");
                }
            }
        }
    }
}
