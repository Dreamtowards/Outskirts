package outskirts.event.world.block;

import outskirts.event.world.WorldEvent;
import outskirts.world.World;
import outskirts.world.chunk.Octree;

public abstract class BlockEvent extends WorldEvent {

    private Octree octree;

    public BlockEvent(World world, Octree octree) {
        super(world);
        this.octree = octree;
    }

    public Octree getOctree() {
        return octree;
    }
}
