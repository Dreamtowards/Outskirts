package outskirts.util.vector;

public class Vector4f extends Vector {

    public static final Vector4f ZERO = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
    public static final Vector4f ONE  = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public static final Vector4f UNIT_X = new Vector4f(1.0f, 0.0f, 0.0f,0.0f);
    public static final Vector4f UNIT_Y = new Vector4f(0.0f, 1.0f, 0.0f,0.0f);
    public static final Vector4f UNIT_Z = new Vector4f(0.0f, 0.0f, 1.0f,0.0f);
    public static final Vector4f UNIT_W = new Vector4f(0.0f, 0.0f, 0.0f,1.0f);

    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f() {}

    public Vector4f(Vector4f src) {
        set(src);
    }

    public Vector4f(float x, float y, float z, float w) {
        set(x, y, z, w);
    }

    public Vector4f set(Vector4f src) {
        return set(src.x, src.y, src.z, src.w);
    }

    public Vector4f set(float x, float y, float z, float w) {
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
    public Vector4f scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }

    @Override
    public Vector4f negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    @Override
    public Vector4f normalize() {
        return (Vector4f) Vector.normalize(this);
    }

    @Override
    public String toString() {
        return "Vector4f[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector4f && ((Vector4f) obj).x == this.x && ((Vector4f) obj).y == this.y && ((Vector4f) obj).z == this.z && ((Vector4f) obj).w == this.w;
    }

    @Override
    public int hashCode() {
        long hash = 0;
        hash = 31L * hash + Float.floatToIntBits(this.x);
        hash = 31L * hash + Float.floatToIntBits(this.y);
        hash = 31L * hash + Float.floatToIntBits(this.z);
        hash = 31L * hash + Float.floatToIntBits(this.w);
        return (int) (hash ^ hash >> 32);
    }

    public Vector4f setX(float x) {
        this.x = x;
        return this;
    }

    public Vector4f setY(float y) {
        this.y = y;
        return this;
    }

    public Vector4f setZ(float z) {
        this.z = z;
        return this;
    }

    public Vector4f setW(float w) {
        this.w = w;
        return this;
    }

    public Vector4f add(Vector4f right) {
        return add(right.x, right.y, right.z, right.w);
    }

    public Vector4f add(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vector4f sub(Vector4f right) {
        return sub(right.x, right.y, right.z, right.w);
    }

    public Vector4f sub(float x, float y, float z, float w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    public static Vector4f add(Vector4f left, Vector4f right, Vector4f dest) {
        if (dest == null)
            dest = new Vector4f();
        return dest.set(
                left.x + right.x,
                left.y + right.y,
                left.z + right.z,
                left.w + right.w
        );
    }

    public static Vector4f sub(Vector4f left, Vector4f right, Vector4f dest) {
        if (dest == null)
            dest = new Vector4f();
        return dest.set(
                left.x - right.x,
                left.y - right.y,
                left.z - right.z,
                left.w - right.w
        );
    }

    public static float dot(Vector4f left, Vector4f right) {
        return left.x * right.x + left.y * right.y + left.z * right.z + left.w * right.w;
    }

    public static float angle(Vector4f a, Vector4f b) {
        return Vector.angle(dot(a, b), a, b);
    }
}
