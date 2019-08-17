package net.porillo.util;

import net.porillo.objects.GChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class ChunkSorter {

    public static List<Chunk> sortByDistance(Chunk[] chunks, Map<GChunk, Integer> waterLevel, List<Player> players, int height, int numChunks) {
        if (players.size() == 0) {
            return Arrays.asList(chunks).subList(0, Math.min(numChunks, chunks.length));
        } else {
            List<Chunk> sortedChunks = new ArrayList<>();
            for (Chunk chunk : chunks) {
                GChunk gchunk = new GChunk(chunk);
                if (waterLevel.containsKey(gchunk)) {
                    int seaLevel = waterLevel.get(gchunk);
                    if (seaLevel == height) {
                        continue;
                    }
                }

                sortedChunks.add(chunk);
            }
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

            return sortedChunks.subList(0, Math.min(numChunks, sortedChunks.size()));
        }
    }

    private static Location chunkToLocation(Chunk chunk) {
        return chunk.getBlock(8, 64, 8).getLocation();
    }
}
