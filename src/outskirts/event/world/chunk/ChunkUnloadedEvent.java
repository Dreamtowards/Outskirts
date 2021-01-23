package outskirts.event.world.chunk;

import outskirts.world.chunk.Chunk;

public class ChunkUnloadedEvent extends ChunkEvent {

    public ChunkUnloadedEvent(Chunk chunk) {
        super(chunk);
    }
}
