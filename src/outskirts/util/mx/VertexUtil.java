package outskirts.util.mx;

import outskirts.client.render.VertexBuffer;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;

public class VertexUtil {

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

    // required CCW Triangles positions.
    public static void hardnorm(VertexBuffer vbuf) {
        // assert vbuf.indices == null;
        Vector3f v1 = new Vector3f(), v2 = new Vector3f(), v3 = new Vector3f(), norm = new Vector3f();
        for (int i = 0;i < vbuf.positions.size();i+=9) {
            Vector3f.set(v1, vbuf.positions::get, i);
            Vector3f.set(v2, vbuf.positions::get, i+3);
            Vector3f.set(v3, vbuf.positions::get, i+6);
            Vector3f.trinorm(v1, v2, v3, norm, null, Vector3f.UNIT_Y);

            for (int j = 0;j < 3;j++) {
                vbuf.normals.set(i+j*3, norm.x);
                vbuf.normals.set(i+j*3+1, norm.y);
                vbuf.normals.set(i+j*3+2, norm.z);
            }
        }
    }

    public static void smoothnorm(VertexBuffer vbuf) {

    }
}
