package outskirts.world;

import outskirts.block.Block;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.storage.Savable;
import outskirts.storage.dst.DObject;
import outskirts.util.Validate;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.io.IOException;

/**
 * A Chunk represents a 16^3 Cubical Space.
 */
public class Chunk implements Savable {

    public static final int SIZE = 16;

    // xyz % 16 must be zero. readonly.
    private final Vector3f position = new Vector3f();

    private final World world;  // rf.

    private final Block[] blocks = new Block[SIZE*SIZE*SIZE];

    public boolean populated = false;  // Unlimited Y Unsupported.

    public Chunk(World world, Vector3f p) {
        validateChunkPos(p);
        this.position.set(p);
        this.world = world;
    }

    public static void validateChunkPos(Vector3f p) {
        Validate.isTrue(p.x%SIZE==0 && p.y%SIZE==0 && p.z%SIZE==0, "Illegal chunk position. %s", p);
    }

    public Block getBlock(int x, int y, int z) {
        return blocks[blidx(x, y, z)];
    }
    public void setBlock(int x, int y, int z, Block block) {
        blocks[blidx(x, y, z)] = block;
    }
    private int blidx(int x, int y, int z) {
        return x << 8 | y << 4 | z;
    }
    public int blidx(Vector3f p) {
        Vector3f u = Vector3f.floor(p, 16);
        return blidx((int)u.x, (int)u.y, (int)u.z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void onRead(DObject mp) throws IOException {

    }

    @Override
    public DObject onWrite(DObject mp) throws IOException {


        return mp;
    }

    public final AABB getAABB(AABB dest) {
        dest.min.set(position);
        dest.max.set(position).add(SIZE);
        return dest;
    }

}
