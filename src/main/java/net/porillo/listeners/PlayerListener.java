package net.porillo.listeners;

import lombok.AllArgsConstructor;
import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.database.queries.update.PlayerUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.PlayerTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;

@AllArgsConstructor
public class PlayerListener implements Listener {

    private GlobalWarming gw;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //Player lookup:
        PlayerTable table = gw.getTableManager().getPlayerTable();
        Player player = event.getPlayer();
        GPlayer gPlayer = table.getOrCreatePlayer(player.getUniqueId());

        //First-time players will receive an instructional booklet:
        // - Note: adding it even if the climate-engine is disabled (in case it is enabled later)
        if (!player.hasPlayedBefore()) {
            ItemStack wiki = new ItemStack(Material.WRITTEN_BOOK);
            final BookMeta meta = (BookMeta) wiki.getItemMeta();
            meta.setDisplayName(Lang.WIKI_NAME.get());
            meta.setAuthor(Lang.WIKI_AUTHOR.get());

            final ArrayList<String> lore = new ArrayList<>();
            lore.add(Lang.WIKI_LORE.get());
            meta.setLore(lore);

            final ArrayList<String> content = new ArrayList<>();
            content.add(Lang.WIKI_INTRODUCTION.get());
            content.add(Lang.WIKI_SCORES.get());
            content.add(Lang.WIKI_EFFECTS.get());
            content.add(Lang.WIKI_BOUNTY.get());
            content.add(Lang.WIKI_OTHER.get());

            //Create the book and add to inventory:
            meta.setPages(content);
            wiki.setItemMeta(meta);
            player.getInventory().addItem(wiki);

            //Notify the player in chat:
            gPlayer.sendMsg(Lang.WIKI_ADDED);
        }

        //Add the scoreboard if the climate engine for the player's associated-world is enabled
        // - Note: scores are not tied to the player's current-world
        WorldClimateEngine engine =
              ClimateEngine.getInstance().getClimateEngine(gPlayer.getAssociatedWorldId());

        if (engine != null && engine.isEnabled()) {
            //Show players their current carbon score in chat:
            player.performCommand("gw score");

            //Add the player to the scoreboard:
            gw.getScoreboard().connect(gPlayer);
        }
    }

    /**
     * When players leave:
     * - Remove them from the scoreboard
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerTable playerTable = gw.getTableManager().getPlayerTable();
        GPlayer gPlayer = playerTable.getPlayers().get(event.getPlayer().getUniqueId());
        gw.getScoreboard().disconnect(gPlayer);
    }

    /**
     * Track a player's current world
     * - This information is needed when players go offline, but have planted saplings,
     * left furnaces running or have active bounties
     */
    @EventHandler
    public void onPlayerChangedWorldEvent​(PlayerChangedWorldEvent event) {
        //Update the player's world:
        PlayerTable playerTable = gw.getTableManager().getPlayerTable();
        GPlayer gPlayer = playerTable.getPlayers().get(event.getPlayer().getUniqueId());
        gPlayer.setWorldId(event.getPlayer().getWorld().getUID());

        //Database update:
        PlayerUpdateQuery updateQuery = new PlayerUpdateQuery(gPlayer);
        AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
    }
}
