package outskirts.physics.collision.shapes.convex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.Transform;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// ConvexHullShape. renamed from PolygonConvexShape.
// note that in the physSystem, the actually Center of Mass are in the local-Origin. so the custom shape should tends makes the CoM in Origin
public class ConvexHullShape extends ConvexShape {

    private Set<Vector3f> vertices = new HashSet<>();  // non duplicated point set.

    public ConvexHullShape(Iterable<Vector3f> vts) {
        for (Vector3f v : vts) {
            if (!vertices.add(v))
                throw new IllegalArgumentException("Duplicated vertex.");
        }
    }

    public Set<Vector3f> getVertices() {
        return vertices;
    }

    /**
     * the most biggest dot value's vertex that dot with d
     */
    @Override
    public Vector3f getFarthestPoint(Vector3f d, Vector3f dest) {
        Vector3f P = null;
        float mxDistan = -Float.MAX_VALUE;
        for (Vector3f v : vertices) {
            float distan = Vector3f.dot(v, d);
            if (distan > mxDistan) {
                mxDistan = distan;
                P = v;
            }
        }
        return dest.set(P);
    }

    private AABB cachedAABB = new AABB();
    private int  cachedAABB_vtshash = 0;
    @Override
    protected AABB getAABB(AABB dest) {
        int vtshash = vertices.hashCode();
        if (cachedAABB_vtshash != vtshash) {
            cachedAABB_vtshash = vtshash;
            AABB.bounding(vertices, cachedAABB);
        }
        return dest.set(cachedAABB);
    }

    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f dest) {
        Vector3f h = AABB.extent(getAABB(cachedAABB, Transform.IDENTITY), dest).scale(1/2f);
        return BoxShape.calculateBoxLocalInertia(mass, h.x, h.y, h.z, dest);
    }
}
