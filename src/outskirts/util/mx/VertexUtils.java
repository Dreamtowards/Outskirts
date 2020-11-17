package outskirts.util.mx;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

public class VertexUtils {

    // needs rename.
    public static float[] centeraabb(float[] positions) {
        Vector3f nCen = AABB.center(AABB.bounding(positions, null), null);  // AABBCenter to Origin.
        for (int i = 0;i < positions.length;i+=3) {
            positions[i]   += -nCen.x;
            positions[i+1] += -nCen.y;
            positions[i+2] += -nCen.z;
        }
        return positions;
    }

    private static void centeravg(float[] positions) {
        throw new UnsupportedOperationException();
    }

    public static float[] scale(float[] positions, float s) {
        for (int i = 0;i < positions.length;i+=3) {
            positions[i]   *= s;
            positions[i+1] *= s;
            positions[i+2] *= s;
        }
        return positions;
    }
}
