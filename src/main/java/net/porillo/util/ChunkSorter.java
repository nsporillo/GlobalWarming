package net.porillo.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class ChunkSorter {

    public static List<Chunk> sortByDistance(Chunk[] chunks, List<Player> players) {
        if (players.size() == 0) {
            return Arrays.asList(chunks);
        } else {
            List<Chunk> sortedChunks = new ArrayList<>();
            Collections.addAll(sortedChunks, chunks);
            sortedChunks.sort((o1, o2) -> {
                Location l1 = chunkToLocation(o1);
                Location l2 = chunkToLocation(o2);
                double d1 = 0;
                double d2 = 0;
                for (Player player : players) {
                    d1 += l1.distance(player.getLocation());
                    d2 += l2.distance(player.getLocation());
                }
                d1 /= players.size();
                d2 /= players.size();

                return Double.compare(d1, d2);
            });

            return sortedChunks;
        }
    }

    private static Location chunkToLocation(Chunk chunk) {
        return chunk.getBlock(8, 64, 8).getLocation();
    }
}
