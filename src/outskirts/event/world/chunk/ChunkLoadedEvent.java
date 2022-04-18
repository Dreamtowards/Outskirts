package outskirts.event.world.chunk;

import outskirts.world.Chunk;

public class ChunkLoadedEvent extends ChunkEvent {

    public ChunkLoadedEvent(Chunk chunk) {
        super(chunk);
    }

}
