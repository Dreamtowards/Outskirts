package outskirts.event.world.chunk.section;

import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.Chunk;

public class SectionUnloadedEvent extends SectionEvent {

    public SectionUnloadedEvent(Chunk chunk, Vector3f positionVal) {
        super(chunk, positionVal);
    }

}
