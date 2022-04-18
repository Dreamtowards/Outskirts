package outskirts.event.world.chunk;

import outskirts.event.world.WorldEvent;
import outskirts.world.Chunk;

public abstract class ChunkEvent extends WorldEvent {

    private Chunk chunk;

    public ChunkEvent(Chunk chunk) {
        super(chunk.getWorld());
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
