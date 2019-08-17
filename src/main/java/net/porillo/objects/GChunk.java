package net.porillo.objects;

import lombok.Data;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;

@Data
public class GChunk {

    private String world;
    private int x, z;

    public GChunk(ChunkSnapshot chunkSnapshot) {
        this.world = chunkSnapshot.getWorldName();
        this.x = chunkSnapshot.getX();
        this.z = chunkSnapshot.getZ();
    }

    public GChunk(Chunk chunk) {
        this.world = chunk.getWorld().getName();
        this.x = chunk.getX();
        this.z = chunk.getZ();
    }
}
