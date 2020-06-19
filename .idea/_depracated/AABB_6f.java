package outskirts.physic;

import outskirts.util.vector.Vector3f;

//TODO: 6float max/minXYZ or 2 min/max Vector3f..?
//2vec3 always better than 6floats, vec3 compact, convenient, clear.
//so first use 6floats, looks what other mainly reason make AABB turn into 2*vec3
public class AABB {

    public float minX;
    public float minY;
    public float minZ;
    public float maxX;
    public float maxY;
    public float maxZ;

    public AABB(float x1, float y1, float z1, float x2, float y2, float z2) {
        minX = Math.min(x1, x2);
        minY = Math.min(y1, y2);
        minZ = Math.min(z1, z2);
        maxX = Math.max(x1, x2);
        maxY = Math.max(y1, y2);
        maxZ = Math.max(z1, z2);
    }

    public AABB(Vector3f p1, Vector3f p2) {
        this(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }

    public boolean contains(float x, float y, float z) {
        return x > minX && x < maxX && y > minY && y < maxY && z > minZ && z < maxZ;
    }

    public boolean containsOrEquals(float x, float y, float z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public boolean intersects(AABB other) {
        return intersectsX(other) && intersectsY(other) && intersectsZ(other);
    }

    public boolean intersectsX(AABB other) {
        return minX < other.maxX && maxX > other.minX;
    }

    public boolean intersectsY(AABB other) {
        return minY < other.maxY && maxY > other.minY;
    }

    public boolean intersectsZ(AABB other) {
        return minZ < other.maxZ && maxZ > other.minZ;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
