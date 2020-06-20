package outskirts.physics.collision.shapes.concave;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConcaveShape;
import outskirts.util.vector.Vector3f;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class TriangleMeshShape extends ConcaveShape {

    private int[] indices;
    private float[] positions;

    public TriangleMeshShape(int[] indices, float[] positions) {
        this.indices = indices;
        this.positions = positions;
    }

    private int getTrianglesCount() {
        return indices.length/3;
    }
    private void getTriangle(int tIdx, Vector3f[] dest) {
        int iIdx = tIdx*3;
        Vector3f.set(dest[0], positions, indices[iIdx  ]*3);
        Vector3f.set(dest[1], positions, indices[iIdx+1]*3);
        Vector3f.set(dest[2], positions, indices[iIdx+2]*3);
    }

    private void getFarPoint(Vector3f d, Vector3f dest) {

        float[] mxDstan = {-Float.MAX_VALUE};

        processAllTriangles((tri, idx) -> {
            for (Vector3f v : tri) {
                float dstan = Vector3f.dot(d, v);
                if (dstan > mxDstan[0]) {
                    mxDstan[0] = dstan;
                    dest.set(v);
                }
            }
        }, AABB.MAX_AABB);

    }

    @Override
    public void processAllTriangles(BiConsumer<Vector3f[], Integer> onProcessTriangle, AABB aabb) {

        Vector3f[] trig = new Vector3f[] {new Vector3f(), new Vector3f(), new Vector3f()};

        for (int i = 0;i < getTrianglesCount();i++) {
            getTriangle(i, trig);

            if (!intersectsTriangleBounding(trig[0], trig[1], trig[2], aabb))
                continue;

            onProcessTriangle.accept(trig, i);
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

    private AABB cachedAABB = new AABB();
    private int  cachedAABB_vtshash = 0;
    @Override
    protected AABB getAABB(AABB dest) {
        int vtshash = Arrays.hashCode(positions);
        if (cachedAABB_vtshash != vtshash) {
            cachedAABB_vtshash = vtshash;

            Vector3f d = new Vector3f();
            Vector3f fp = new Vector3f();
            for (int i = 0;i < 3;i++) {
                // max at axis.
                Vector3f.set(d.set(0,0,0), i, 1);
                getFarPoint(d, fp);
                Vector3f.set(cachedAABB.max, i, Vector3f.get(fp, i));
                // min at axis.
                d.negate();
                getFarPoint(d, fp);
                Vector3f.set(cachedAABB.min, i, Vector3f.get(fp, i));
            }
        }
        return dest.set(cachedAABB);
    }
}
