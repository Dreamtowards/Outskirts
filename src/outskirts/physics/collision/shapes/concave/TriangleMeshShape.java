package outskirts.physics.collision.shapes.concave;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConcaveShape;
import outskirts.util.vector.Vector3f;

import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * Triangle vertices data. localspace.
 */
//todo: setMesh(indices, positions) .?
public abstract class TriangleMeshShape extends ConcaveShape {

    /**
     * indices of positions vector. index unit is vec3. (not scalar).
     * idxValue = [0, positions.length/3)
     */
    private int[] indices;

    /**  positions vec3 table. unit as vec3. */
    private float[] positions;

    protected Vector3f[] TMP_TRIANGE = new Vector3f[] {new Vector3f(), new Vector3f(), new Vector3f()};

    public TriangleMeshShape(int[] indices, float[] positions) {
        setMesh(indices, positions);
    }

    private final void setMesh(int[] indices, float[] positions) {
        assert indices.length % 3 == 0 && positions.length % 3 == 0;
        this.indices = indices;
        this.positions = positions;

        processMeshModified();
    }

    protected void processMeshModified() {

        recalcAABB();
    }

    protected int trianglesCount() {
        return indices.length/3;
    }
    protected Vector3f[] getTriangle(int trianglIndex, Vector3f[] dest) {
        int v0idx = trianglIndex*3;
        Vector3f.set(dest[0], positions, indices[v0idx  ]*3);
        Vector3f.set(dest[1], positions, indices[v0idx+1]*3);
        Vector3f.set(dest[2], positions, indices[v0idx+2]*3);
        return dest;
    }

    @Override
    public void collideTriangles(AABB aabb, BiConsumer<Integer, Vector3f[]> oncollide) {
        Vector3f[] tmpTrig = TMP_TRIANGE;

        for (int i = 0;i < trianglesCount();i++) {
            getTriangle(i, tmpTrig);

            if (!intersectsTriangleBounding(tmpTrig[0], tmpTrig[1], tmpTrig[2], aabb))
                continue;

            oncollide.accept(i, tmpTrig);
        }
    }

    // inline-version of: aabb.intersects(AABB.bounding(points, tmpAABB))
    private static boolean intersectsTriangleBounding(Vector3f v0, Vector3f v1, Vector3f v2, AABB aabb) {

        if (Math.min(Math.min(v0.x, v1.x), v2.x) > aabb.max.x) return false;
        if (Math.min(Math.min(v0.y, v1.y), v2.y) > aabb.max.y) return false;
        if (Math.min(Math.min(v0.z, v1.z), v2.z) > aabb.max.z) return false;

        if (Math.max(Math.max(v0.x, v1.x), v2.x) < aabb.min.x) return false;
        if (Math.max(Math.max(v0.y, v1.y), v2.y) < aabb.min.y) return false;
        if (Math.max(Math.max(v0.z, v1.z), v2.z) < aabb.min.z) return false;

        return true;
    }

    private void getfarpoint(Vector3f d, Vector3f dest) {
        float mxDstan = -Float.MAX_VALUE;
        for (int i = 0;i < positions.length;i+=3) {
            Vector3f v = TMP_TRIANGE[0].set(positions[i], positions[i+1], positions[i+2]);
            float dstan = Vector3f.dot(d, v);
            if (dstan > mxDstan) {
                mxDstan = dstan;
                dest.set(v);
            }
        }
    }

    private AABB cachedAABB = new AABB();
    private void recalcAABB() {
        Vector3f d = new Vector3f();
        Vector3f fp = new Vector3f();
        for (int i = 0;i < 3;i++) {
            // max at axis.
            Vector3f.set(d.set(0,0,0), i, 1);
            getfarpoint(d, fp);
            Vector3f.set(cachedAABB.max, i, Vector3f.get(fp, i));
            // min at axis.
            d.negate();
            getfarpoint(d, fp);
            Vector3f.set(cachedAABB.min, i, Vector3f.get(fp, i));
        }
    }

    @Override
    protected AABB getAABB(AABB dest) {
        return dest.set(cachedAABB);
    }
}
