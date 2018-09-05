package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.porillo.GlobalWarming;
import net.porillo.database.tables.OffsetTable;
import net.porillo.objects.GPlayer;
import net.porillo.objects.OffsetBounty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("globalwarming|gw")
public class GeneralCommands extends BaseCommand {

    @Subcommand("score")
    @Description("Get your carbon score")
    @CommandPermission("globalwarming.score")
    public void onScore(GPlayer gPlayer) {
        Player player = Bukkit.getPlayer(gPlayer.getUuid());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Your current carbon footprint is " + formatScore(gPlayer.getCarbonScore()));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Your goal is to keep your score as close to zero as possible!");
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

    @Subcommand("bounty")
    @CommandPermission("globalwarming.bounty")
    public class BountyCommand extends BaseCommand {

        @Subcommand("offset")
        @Description("Set tree-planting bounties to reduce carbon footprint")
        @Syntax("[log] [reward]")
        @CommandPermission("globalwarming.bounty.offset")
        public void onBountyOffset(GPlayer gPlayer, String[] args) {
            // Validate input
            Integer logTarget;
            Integer reward;

            try {
                logTarget = Integer.parseInt(args[0]);
                reward = Integer.getInteger(args[1]);

                if (logTarget <= 0 || reward <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                gPlayer.sendMsg(ChatColor.RED + "Error: <trees> and <reward> must be positive integers");
                return;
            }

            //TODO: Add economy integration
            OffsetBounty bounty = new OffsetBounty();
            bounty.setCreator(gPlayer);
            bounty.setLogBlocksTarget(logTarget);
            bounty.setReward(reward);
        }

        // TODO: When listing bounties, add a clickable chat link to easily start job
        // TODO: Add configurable player max concurrent bounties to prevent bounty hoarding
        @Subcommand("list")
        @Description("Show all current bounties")
        @Syntax("")
        @CommandPermission("globalwarming.bounty.list")
        public void onBounty(GPlayer gPlayer, String[] args) {
            OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
            Player player = gPlayer.getPlayer();

            int numBounties = offsetTable.getOffsetList().size();
            gPlayer.sendMsg(ChatColor.GREEN + "Showing " + numBounties + " Tree Planting Bounties");

            // TODO: Paginate if necessary
            for (OffsetBounty bounty : offsetTable.getOffsetList()) {
                if (bounty.isAvailable()) {
                    bounty.showPlayerDetails(player);
                }
            }
        }

    }

    @HelpCommand
    public void onHelp(GPlayer gPlayer, CommandHelp help) {
        help.showHelp();
    }

}
