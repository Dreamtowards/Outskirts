package outskirts.util.vector;

public class Vector3i extends Vector {

    public static final Vector3i ZERO = new Vector3i(0, 0, 0);

    public int x;
    public int y;
    public int z;

    public Vector3i() {}

    public Vector3i(Vector3i src) {
        set(src);
    }

    public Vector3i(int x, int y, int z) {
        set(x, y, z);
    }

    public Vector3i set(Vector3i src) {
        return set(src.x, src.y, src.z);
    }

    public Vector3i set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    @Override
    public Vector3i scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    @Override
    public Vector3i negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    @Override
    public Vector3i normalize() {
        return (Vector3i) Vector.normalize(this);
    }

    @Override
    public String toString() {
        return "Vector3i[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector3i && ((Vector3i) obj).x == this.x && ((Vector3i) obj).y == this.y && ((Vector3i) obj).z == this.z;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.y;
        hash = 31 * hash + this.z;
        return hash ^ hash >> 16;
    }

    public Vector3i setX(int x) {
        this.x = x;
        return this;
    }

    public Vector3i setY(int y) {
        this.y = y;
        return this;
    }

    public Vector3i setZ(int z) {
        this.z = z;
        return this;
    }

    public Vector3i add(Vector3i right) {
        return add(right.x, right.y, right.z);
    }

    public Vector3i add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3i sub(Vector3i right) {
        return sub(right.x, right.y, right.z);
    }

    public Vector3i sub(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
}
