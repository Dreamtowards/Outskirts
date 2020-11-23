package outskirts.event.world.chunk;

import outskirts.world.chunk.Chunk;

public class ChunkMeshBuildedEvent extends ChunkEvent {

    public ChunkMeshBuildedEvent(Chunk chunk) {
        super(chunk);
    }
}
