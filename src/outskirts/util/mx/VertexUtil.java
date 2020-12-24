package outskirts.util.mx;

import outskirts.client.render.VertexBuffer;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

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
    public static void hardnorm(VertexBuffer vbuf, int begini, int endi) {
        // assert vbuf.indices == null;
        Vector3f v1 = new Vector3f(), v2 = new Vector3f(), v3 = new Vector3f(), norm = new Vector3f();
        for (int i = begini;i < endi;i+=9) {
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
    public static void hardnorm(VertexBuffer vbuf, int begini) {
        VertexUtil.hardnorm(vbuf, begini, vbuf.normals.size());
    }
    public static void hardnorm(VertexBuffer vbuf) {
        VertexUtil.hardnorm(vbuf, 0);
    }

    // Avg the same positions's normals.
    public static void smoothnorm(VertexBuffer vbuf) {
        List<Vector3f> excludedpos = new ArrayList<>();
        List<Integer> foundvtx = new ArrayList<>();
        // assert vbuf.indices == null;
        Vector3f vpos = new Vector3f(), vfindtmp = new Vector3f(), avgNorm = new Vector3f();
        for (int vIdx = 0;vIdx < vbuf.positions.size();vIdx+=3) { // for Vertices
            Vector3f.set(vpos, vbuf.positions::get, vIdx);
            if (excludedpos.contains(vpos)) continue;

            foundvtx.clear();
            foundvtx.add(vIdx);

            // do Search.
            for (int i = 0;i < vbuf.positions.size();i+=3) {
                Vector3f.set(vfindtmp, vbuf.positions::get, i);
                if (vfindtmp.equals(vpos))
                    foundvtx.add(i);
            }
            // do AVG.
            avgNorm.set(0,0,0);
            for (int i : foundvtx) {
                Vector3f.set(vfindtmp, vbuf.normals::get, i);
                avgNorm.add(vfindtmp);
            }
            avgNorm.scale(1f / foundvtx.size());

            // set Them
            for (int i : foundvtx) {
                vbuf.normals.set(i, avgNorm.x);
                vbuf.normals.set(i+1, avgNorm.y);
                vbuf.normals.set(i+2, avgNorm.z);
            }

            excludedpos.add(new Vector3f(vpos));
        }

    }
}
