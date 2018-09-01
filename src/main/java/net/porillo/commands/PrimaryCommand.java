package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.porillo.GlobalWarming;
import net.porillo.database.tables.OffsetTable;
import net.porillo.objects.GPlayer;
import net.porillo.objects.OffsetBounty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("globalwarming|gw")
public class PrimaryCommand extends BaseCommand {

    @Subcommand("score")
    @Description("Get your carbon score")
    @CommandPermission("globalwarming.score")
    public void onScore(GPlayer gPlayer) {
        Player player = Bukkit.getPlayer(gPlayer.getUuid());
        player.sendMessage("Your current carbon footprint is " + formatScore(gPlayer.getCarbonScore()));
        player.sendMessage("Your goal is to keep your score as close to zero as possible!");
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


    @Subcommand("offset")
    @Syntax("[log target] [reward]")
    @Description("Set tree-planting bounties to reduce carbon footprint")
    @CommandPermission("globalwarming.offset")
    public void onBountyOffset(GPlayer gPlayer, String[] args) {
        Player player = Bukkit.getPlayer(gPlayer.getUuid());
        // Validate input
        Integer logTarget = null;
        Integer reward = null;

        try {
            logTarget = Integer.parseInt(args[0]);
            reward = Integer.getInteger(args[1]);

            if (logTarget <= 0 || reward <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage(ChatColor.RED + "Error: <trees> and <reward> must be positive integers");
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
    @Subcommand("offset")
    @Description("Starts a tree-planting bounty job.")
    @CommandPermission("globalwarming.bounty")
    public void onBounty(GPlayer gPlayer, String[] args) {
        OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
        Player player = Bukkit.getPlayer(gPlayer.getUuid());

        if (args.length == 0) {
            int numBounties = offsetTable.getOffsetList().size();
            player.sendMessage(ChatColor.GREEN + "Showing " + numBounties + " Tree Planting Bounties");

            // TODO: Paginate if necessary
            for (OffsetBounty bounty : offsetTable.getOffsetList()) {
                if (bounty.isAvailable()) {
                    bounty.showPlayerDetails(player);
                }
            }
        }
    }

}
