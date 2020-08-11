package outskirts.util.mex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

public class VerticesUtils {

    public static void alignAabbCenter(float[] positions) {
        Vector3f nCen = AABB.center(AABB.bounding(positions, null), null).negate();  // AABBCenter to Origin.
        for (int i = 0;i < positions.length/3;i++) {
            positions[i*3]   += nCen.x;
            positions[i*3+1] += nCen.y;
            positions[i*3+2] += nCen.z;
        }
    }

}
