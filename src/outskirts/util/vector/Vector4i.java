package outskirts.util.vector;

public class Vector4i extends Vector {

    public static final Vector4i ZERO = new Vector4i(0, 0, 0, 0);

    public int x;
    public int y;
    public int z;
    public int w;

    public Vector4i() {}

    public Vector4i(Vector4i src) {
        set(src);
    }

    public Vector4i(int x, int y, int z, int w) {
        set(x, y, z, w);
    }

    public Vector4i set(Vector4i src) {
        return set(src.x, src.y, src.z, src.w);
    }

    public Vector4i set(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    @Override
    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    @Override
    public Vector4i scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }

    @Override
    public Vector4i negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    @Override
    public Vector4i normalize() {
        return (Vector4i) Vector.normalize(this);
    }

    @Override
    public String toString() {
        return "Vector4i[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector4i && ((Vector4i) obj).x == this.x && ((Vector4i) obj).y == this.y && ((Vector4i) obj).z == this.z && ((Vector4i) obj).w == this.w;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.y;
        hash = 31 * hash + this.z;
        hash = 31 * hash + this.w;
        return hash ^ hash >> 16;
    }

    public Vector4i setX(int x) {
        this.x = x;
        return this;
    }

    public Vector4i setY(int y) {
        this.y = y;
        return this;
    }

    public Vector4i setZ(int z) {
        this.z = z;
        return this;
    }

    public Vector4i setW(int w) {
        this.w = w;
        return this;
    }

    public Vector4i add(Vector4i right) {
        return add(right.x, right.y, right.z, right.w);
    }

    public Vector4i add(int x, int y, int z, int w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vector4i sub(Vector4i right) {
        return sub(right.x, right.y, right.z, right.w);
    }

    public Vector4i sub(int x, int y, int z, int w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }
}
