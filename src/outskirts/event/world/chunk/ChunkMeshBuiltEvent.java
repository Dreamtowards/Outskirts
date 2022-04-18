package outskirts.event.world.chunk;

import outskirts.world.Chunk;

/**
 * for now, jus use by MapRenderer.
 */
public class ChunkMeshBuiltEvent extends ChunkEvent {

    public ChunkMeshBuiltEvent(Chunk chunk) {
        super(chunk);
    }
}
