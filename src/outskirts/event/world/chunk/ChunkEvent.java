package outskirts.event.world.chunk;

import outskirts.event.world.WorldEvent;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;

public abstract class ChunkEvent extends WorldEvent {

    private Chunk chunk;

    public ChunkEvent(Chunk chunk) {
//        super(chunk.getWorld());
        super(null);
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
