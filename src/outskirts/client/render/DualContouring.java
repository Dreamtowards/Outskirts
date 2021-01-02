package outskirts.client.render;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.function.TrifFunc;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.util.*;

/**
 * Dual Contouring.
 *
 * 1. notonly f(v), but also needs its gradient. which f'(v) -> vec.
 *
 * 2. for each edge which had sign-change. get the normal of the iso-intersect-point.
 *
 * 3. for each cell which had sign-change edge. compute the interior point by those surrounding edge's p-Normal.
 *    using quadratic function. QEF method.
 *
 * 4. for each edge which had sign-change, for the 4 cells which adjacent of that edge, connect the 4 vertices.
 *
 */
public final class DualContouring {

    private static final float DEF_D = 0.01f;

    public static final TrifFunc F_SPHERE = (x, y, z) ->
            2.5f - (float)Math.sqrt(x*x + y*y + z*z);

    private static Vector3f findcellvertex(TrifFunc f, Vector3f p, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
        if (true)
            return dest.set(p).addScaled(.5f, Vector3f.ONE);

        // evaluate f value at 8 corner-vert.
        float[][][] v = new float[2][2][2];
        for (int dx=0; dx<2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                for (int dz = 0; dz < 2; dz++) {
                    v[dx][dy][dz] = f.sample(p.x+dx, p.y+dy, p.z+dz);
                }
            }
        }

        // find sign-change edges. in cube cell 12 edges.
        // for each 4 edges in 3 axies.
        List<Vector3f> edgeverts = new ArrayList<>();
        for (int dy=0; dy<2; dy++) {  // X AXIS EDGES.v
            for (int dz=0; dz<2; dz++) {
                if ((v[0][dy][dz]>0) != (v[1][dy][dz]>0))
                    edgeverts.add(new Vector3f(p.x+Maths.inverseLerp(0, v[0][dy][dz], v[1][dy][dz]), p.y+dy, p.z+dz));
            }
        }
        for (int dx=0; dx<2; dx++) {
            for (int dz=0; dz<2; dz++) {
                if ((v[dx][0][dz]>0) != (v[dx][1][dz]>0))
                    edgeverts.add(new Vector3f(p.x+dx, p.y+Maths.inverseLerp(0, v[dx][0][dz], v[dx][1][dz]), p.z+dz));
            }
        }
        for (int dx=0; dx<2; dx++) {
            for (int dy=0; dy<2; dy++) {
                if ((v[dx][dy][0]>0) != (v[dx][dy][1]>0))
                    edgeverts.add(new Vector3f(p.x+dx, p.y+dy, p.z+Maths.inverseLerp(0, v[dx][dy][0], v[dx][dy][1])));
            }
        }

        if (edgeverts.size() < 2)
            return null;

        List<Vector3f> vertnorms = new ArrayList<>(edgeverts.size());
        for (Vector3f vert : edgeverts)
            vertnorms.add(fnorm(f, vert, null));

        return solveqef(p, edgeverts, vertnorms, dest);
    }

    private static Vector3f solveqef(Vector3f p, List<Vector3f> verts, List<Vector3f> norms, Vector3f dest) {

        return dest;
    }

    /**
     *  Approximated Gradient. i.e. the f'(v).
     *  f'(x, y, z) = normalize(
     *      (f(x+d,y,z) - f(x-d,y,z)) / 2d,
     *      (f(x,y+d,z) - f(x,y-d,z)) / 2d,
     *      (f(x,y,z+d) - f(x,y,z-d)) / 2d
     *   )
     */
    public static Vector3f fnorm(TrifFunc f, Vector3f p, Vector3f dest, float d) {
        if (dest == null) dest = new Vector3f();
        float denom = 1f / (2f*d);
        return dest.set(
                (f.sample(p.x+d, p.y, p.z) - f.sample(p.x-d, p.y, p.z)) * denom,
                (f.sample(p.x, p.y+d, p.z) - f.sample(p.x, p.y-d, p.z)) * denom,
                (f.sample(p.x, p.y, p.z+d) - f.sample(p.x, p.y, p.z-d)) * denom
        ).normalize();
    }
    public static Vector3f fnorm(TrifFunc f, Vector3f p, Vector3f dest) {
        return fnorm(f, p, dest, DEF_D);
    }

    private static class QuadFace {
        private Vector3f v1, v2, v3, v4;
        private QuadFace(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, boolean swap) {
            if (swap) { this.v1=v4; this.v2=v3; this.v3=v2; this.v4=v1; }
            else {      this.v1=v1; this.v2=v2; this.v3=v3; this.v4=v4; }
        }
        private Vector3f[] tri() {
            return new Vector3f[] { new Vector3f(v1), new Vector3f(v2), new Vector3f(v3),
                                    new Vector3f(v3), new Vector3f(v4), new Vector3f(v1)};
        }
    }

    public static Vector3f[] contouring(TrifFunc f, AABB aabb) {

        // for each 'valid' cell, find the interior vertex.
        Map<Vector3f, Vector3f> verts = new HashMap<>();
        for (float x=aabb.min.x; x<aabb.max.x; x++) {
            for (float y=aabb.min.y; y<aabb.max.y; y++) {
                for (float z=aabb.min.z; z<aabb.max.z; z++) {
                    Vector3f p = new Vector3f(x, y, z);
                    Vector3f vert = findcellvertex(f, p, null);
                    if (vert != null)  // valid cell.
                        verts.put(p, vert);
                }
            }
        }

        // for each sign-change edge, connect its 4 adjacent cells vertex. CCW winding.
        List<QuadFace> faces = new ArrayList<>();
        boolean solid0, solid1;
        for (float x=aabb.min.x; x<aabb.max.x; x++) {
            for (float y=aabb.min.y; y<aabb.max.y; y++) {
                for (float z=aabb.min.z; z<aabb.max.z; z++) {
                    solid0 = f.sample(x,y,z) > 0;
                    if (y > aabb.min.y && z > aabb.min.z) {  // X AXIS. edge.
                        solid1 = f.sample(x+1,y,z) > 0;
                        if (solid0 != solid1)  // is sign-change edge.
                            faces.add(new QuadFace(
                                    new Vector3f(x, y, z-1),
                                    new Vector3f(x, y, z),
                                    new Vector3f(x, y-1, z),
                                    new Vector3f(x, y-1, z-1), solid1
                            ));
                    }
                    if (x > aabb.min.x && z > aabb.min.z) {  // Y AXIS.
                        solid1 = f.sample(x, y+1, z) > 0;
                        if (solid0 != solid1)
                            faces.add(new QuadFace(
                                    new Vector3f(x, y, z-1),
                                    new Vector3f(x-1, y, z-1),
                                    new Vector3f(x-1, y, z),
                                    new Vector3f(x, y, z), solid1
                            ));
                    }
                    if (x > aabb.min.x && y > aabb.min.y) {  // Z-AXIS.
                        solid1 = f.sample(x, y, z+1) > 0;
                        if (solid0 != solid1)
                            faces.add(new QuadFace(
                                    new Vector3f(x, y, z),
                                    new Vector3f(x-1, y, z),
                                    new Vector3f(x-1, y-1, z),
                                    new Vector3f(x, y-1, z), solid1
                            ));
                    }
                }
            }
        }

        for (QuadFace q : faces) {
            q.v1.set(verts.get(q.v1));
            q.v2.set(verts.get(q.v2));
            q.v3.set(verts.get(q.v3));
            q.v4.set(verts.get(q.v4));
        }

        Vector3f[] trils = new Vector3f[faces.size() * 6];
        for (int i = 0;i < faces.size();i++) {
            System.arraycopy(faces.get(i).tri(), 0, trils, i*6, 6);
        }
        return trils;
    }

}
