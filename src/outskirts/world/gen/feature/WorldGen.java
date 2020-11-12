package outskirts.world.gen.feature;

import outskirts.util.vector.Vector3f;
import outskirts.world.World;

/**
 * Populate/Decoration phase.
 */
public abstract class WorldGen {

    public abstract boolean generate(World world, Vector3f blockpos);

}
