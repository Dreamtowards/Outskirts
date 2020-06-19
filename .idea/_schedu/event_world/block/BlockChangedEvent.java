package outskirts.event.world.block;

import outskirts.world.World;
import outskirts.world.chunk.Octree;

/**
 * Note that this event is only be post from World::setBlockState() //should be..?
 * actually post from Chunk::setBlockState will more comprehensive (that can receives almost all BlockChanges)
 * but some times, some operation needs Big-Quantity block change, e.g terrain generation, that'll needs a lots event post time
 * dont know should all BlockChanges event be post? or just post World::setBlockState just ok..?
 */
public class BlockChangedEvent extends BlockEvent {

    public BlockChangedEvent(World world, Octree octree) {
        super(world, octree);
    }
}
