package outskirts.event.world.chunk.section;

import outskirts.event.world.chunk.ChunkEvent;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.Chunk;

public abstract class SectionEvent extends ChunkEvent {

    private Vector3f sectionPosition = new Vector3f();

    public SectionEvent(Chunk chunk, Vector3f positionVal) {
        super(chunk);
        sectionPosition.set(positionVal);
    }

    public Vector3f getPosition() {
        return sectionPosition;
    }
}
