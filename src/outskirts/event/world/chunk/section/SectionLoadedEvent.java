package outskirts.event.world.chunk.section;

import outskirts.event.world.chunk.ChunkEvent;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.Chunk;

public class SectionLoadedEvent extends SectionEvent {

    public SectionLoadedEvent(Chunk chunk, Vector3f positionVal) {
        super(chunk, positionVal);
    }

}
