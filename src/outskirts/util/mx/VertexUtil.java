package outskirts.util.mx;

import outskirts.client.render.VertexBuffer;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.util.*;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;

/* (Shinked Util name.)  */
public class VertexUtil {

    public static void validTriangle(List<Float> positions, int begin) {
        Vector3f v1=vec3(positions, begin), v2=vec3(positions,begin+3), v3=vec3(begin+6);
        if (v1.equals(v2) && v1.equals(v3)) { // really triangle. distinct points. v1 != v2 && v1 != v3

        }
    }

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
    public static Vector3f centeravg(Collection<Vector3f> vts, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
        dest.set(0, 0, 0);
        for (Vector3f v : vts) {
            dest.add(v);
        }
        return dest.scale(1f / vts.size());
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
            vbuf.getpos(i, v1);
            vbuf.getpos(i+3, v2);
            vbuf.getpos(i+6, v3);

            try {
                Vector3f.trinorm(v1, v2, v3, norm);
            } catch (ArithmeticException ex) {
                Log.LOGGER.info("fail calc trinorm.");
                norm.set(Vector3f.UNIT_Y);
            }

            vbuf.setnorm(i, norm);
            vbuf.setnorm(i+3, norm);
            vbuf.setnorm(i+6, norm);
        }
    }
    public static void hardnorm(VertexBuffer vbuf, int begini) {
        assert vbuf.positions.size() == vbuf.normals.size();
        VertexUtil.hardnorm(vbuf, begini, vbuf.normals.size());
    }
    public static void hardnorm(VertexBuffer vbuf) {
        VertexUtil.hardnorm(vbuf, 0);
    }

    // angle weighted shared-pos norms avg. but have avg AngelControl.
    public static void smoothnorm(VertexBuffer vbuf) {
        Map<Vector3f, List<Vector3f>> vnorms = new HashMap<>();
        Vector3f v1=new Vector3f(), v2=new Vector3f(), v3=new Vector3f();
        Vector3f angv1=new Vector3f(), angv2=new Vector3f();
        Vector3f norm = new Vector3f();
        Vector3f avgnorm = new Vector3f();
        for (int i=0; i<vbuf.positions.size(); i+=9) {
            vbuf.getpos(i, v1);
            vbuf.getpos(i+3, v2);
            vbuf.getpos(i+6, v3);

            try {
                Vector3f.trinorm(v1, v2, v3, norm);
            } catch (ArithmeticException ex) {
                norm.set(Vector3f.UNIT_Y);
//                Log.LOGGER.info("fail calc tri norm.");
            }

            float a1 = Vector3f.angle(Vector3f.sub(v2, v1, angv1), Vector3f.sub(v3, v1, angv2));
            float a2 = Vector3f.angle(Vector3f.sub(v3, v2, angv1), Vector3f.sub(v1, v2, angv2));
            float a3 = Vector3f.angle(Vector3f.sub(v1, v3, angv1), Vector3f.sub(v2, v3, angv2));

            norm.normalize();   // normalize if ignore area weight.

            snPutNorm(vnorms, v1, new Vector3f(norm).scale(a1));
            snPutNorm(vnorms, v2, new Vector3f(norm).scale(a2));
            snPutNorm(vnorms, v3, new Vector3f(norm).scale(a3));

            vbuf.setnorm(i, norm);
            vbuf.setnorm(i+3, norm);
            vbuf.setnorm(i+6, norm);
        }

        for (int i=0; i<vbuf.positions.size(); i+=3) {
            Vector3f.set(v1, vbuf.positions::get, i);
            Vector3f.set(norm, vbuf.normals::get, i);

            avgnorm.set(0,0,0);
            for (Vector3f n : vnorms.get(v1)) {
                if (Vector3f.angle(norm, n) < Math.toRadians(45))
                    avgnorm.add(n);
            }
            try {
                avgnorm.normalize();
            } catch (ArithmeticException ex) {
                norm.set(Vector3f.UNIT_Y);
                Log.LOGGER.info("fail norm. no valid avg.");
            }

            vbuf.normals.set(i,   avgnorm.x);
            vbuf.normals.set(i+1, avgnorm.y);
            vbuf.normals.set(i+2, avgnorm.z);
        }
    }

    private static void snPutNorm(Map<Vector3f, List<Vector3f>> vnorms, Vector3f k, Vector3f n) {
        List<Vector3f> nms = vnorms.get(k);
        if (nms == null)
            vnorms.put(new Vector3f(k), nms=new ArrayList<>());
        nms.add(n);
    }
}
