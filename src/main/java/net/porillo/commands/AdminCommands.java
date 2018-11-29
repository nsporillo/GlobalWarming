package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.porillo.GlobalWarming;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.effect.EffectEngine;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.change.block.BlockChange;
import net.porillo.effect.api.change.block.SyncChunkUpdateTask;
import net.porillo.effect.negative.SeaLevelRise;
import net.porillo.objects.GPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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
                gPlayer.sendMsg(String.format("%sDatabase console logging = %strue.", ChatColor.GREEN, ChatColor.YELLOW));
            } else {
                gPlayer.sendMsg(String.format("%sDatabase console logging = %sfalse.", ChatColor.GREEN, ChatColor.GRAY));
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

                    Player player = gPlayer.getPlayer();
                    Supplier<HashSet<BlockChange>> changes = EffectEngine.getInstance().getEffect(SeaLevelRise.class, ClimateEffectType.SEA_LEVEL_RISE).execute(player.getLocation().getChunk().getChunkSnapshot(), seaLevel);
                    new SyncChunkUpdateTask(player.getLocation().getChunk(), CompletableFuture.supplyAsync(changes)).runTaskLater(GlobalWarming.getInstance(), 40L);
                    gPlayer.sendMsg(ChatColor.GREEN + String.format("Applying sea level rise from y:%d to chunk", seaLevel));
                } else {
                    gPlayer.sendMsg(ChatColor.RED + "Invalid Args");
                }
            }
        }
    }
}

