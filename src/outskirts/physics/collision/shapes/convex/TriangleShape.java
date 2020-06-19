package outskirts.physics.collision.shapes.convex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.vector.Vector3f;

public class TriangleShape extends ConvexShape {

    private final Vector3f[] vertices = new Vector3f[] {new Vector3f(), new Vector3f(), new Vector3f()};

    public TriangleShape() { }

    public TriangleShape(Vector3f v0, Vector3f v1, Vector3f v2) {
        setVertices(v0, v1, v2);
    }

    public Vector3f[] getVertices() {
        return vertices;
    }
    public TriangleShape setVertices(Vector3f v0, Vector3f v1, Vector3f v2) {
        vertices[0]=v0;
        vertices[1]=v1;
        vertices[2]=v2;
        return this;
    }

    @Override
    public Vector3f getFarthestPoint(Vector3f d, Vector3f dest) {
        float dA = Vector3f.dot(d, vertices[0]);
        float dB = Vector3f.dot(d, vertices[1]);
        float dC = Vector3f.dot(d, vertices[2]);
        return dest.set(vertices[dA>dB ? (dA>dC ? 0 : 2) : (dB>dC ? 1 : 2)]);
    }

    @Override
    protected AABB getAABB(AABB dest) {
        return AABB.bounding(vertices, dest);
    }

    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f dest) {
        return dest.set(0, 0, 0);
    }
}
