package outskirts.event.world.chunk;

import outskirts.world.chunk.Chunk;

public class ChunkLoadedEvent extends ChunkEvent {

    public ChunkLoadedEvent(Chunk chunk) {
        super(chunk);
    }

}
