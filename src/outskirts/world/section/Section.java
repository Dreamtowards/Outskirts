package outskirts.world.section;

import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

/**
 * Since Chunk been 3-axis-unlimited,
 *  how handles Entities stroage.? and 2d biome.
 */
public class Section {

    private Octree octree;

    private final Vector3f position = new Vector3f();  // base. xyz % 16 == 0.

    private World world;  // ref.

    private boolean populated;


    public Section(World world, Vector3f positionv) {
        this.world = world;
        this.position.set(positionv);
    }


    public World getWorld() {
        return world;
    }

    public Vector3f getPosition() {
        return position;
    }
}
