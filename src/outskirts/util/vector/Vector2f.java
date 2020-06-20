package outskirts.util.vector;

public class Vector2f extends Vector {

    public static final Vector2f ZERO = new Vector2f(0.0f, 0.0f);
    public static final Vector2f ONE  = new Vector2f(1.0f, 1.0f);

    public static final Vector2f UNIT_X = new Vector2f(1.0f, 0.0f);
    public static final Vector2f UNIT_Y = new Vector2f(0.0f, 1.0f);

    public float x;
    public float y;

    public Vector2f() {}

    public Vector2f(Vector2f src) {
        set(src);
    }

    public Vector2f(float x, float y) {
        set(x, y);
    }

    public Vector2f set(Vector2f src) {
        return set(src.x, src.y);
    }

    public Vector2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public float lengthSquared() {
        return x * x + y * y;
    }

    @Override
    public Vector2f scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    @Override
    public Vector2f negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    @Override
    public Vector2f normalize() {
        return (Vector2f) Vector.normalize(this);
    }

    @Override
    public String toString() {
        return "Vector2f[" + this.x + ", " + this.y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector2f && ((Vector2f) obj).x == this.x && ((Vector2f) obj).y == this.y;
    }

    @Override
    public int hashCode() {
        long hash = 0;
        hash = 31L * hash + Float.floatToIntBits(this.x);
        hash = 31L * hash + Float.floatToIntBits(this.y);
        return (int) (hash ^ hash >> 32);
    }

    public Vector2f setX(float x) {
        this.x = x;
        return this;
    }

    public Vector2f setY(float y) {
        this.y = y;
        return this;
    }

    public Vector2f add(Vector2f right) {
        return add(right.x, right.y);
    }

    public Vector2f add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public final Vector2f addScaled(float scalar, Vector2f v) {
        this.x += scalar * v.x;
        this.y += scalar * v.y;
        return this;
    }

    public Vector2f sub(Vector2f right) {
        return sub(right.x, right.y);
    }

    public Vector2f sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public static Vector2f add(Vector2f left, Vector2f right, Vector2f dest) {
        if (dest == null)
            dest = new Vector2f();
        return dest.set(
                left.x + right.x,
                left.y + right.y
        );
    }

    public static Vector2f sub(Vector2f left, Vector2f right, Vector2f dest) {
        if (dest == null)
            dest = new Vector2f();
        return dest.set(
                left.x - right.x,
                left.y - right.y
        );
    }

    public static float dot(Vector2f left, Vector2f right) {
        return left.x * right.x + left.y * right.y;
    }

    public static float angle(Vector2f a, Vector2f b) {
        return Vector.angle(dot(a, b), a, b);
    }


    /**
     * reflect dest around the normal
     * r = d+ -(d·n)*n*2
     * @param norm normalized normal vector
     * @param dest src vector, and will as output
     */
    private static Vector2f reflect(Vector2f norm, Vector2f dest) {
        float projlen2 = -Vector2f.dot(norm, dest) * 2f;
        return dest.set(
                dest.x + projlen2*norm.x,
                dest.y + projlen2*norm.y
        );
    }







    public static final int SIZE = 2;  // "length" Misleading. "components" HighLv.d
    public static float get(Vector2f src, int i) {
        if (i==0) return src.x;
        if (i==1) return src.y;
        throw new IndexOutOfBoundsException();
    }
    public static float set(Vector2f dest, float value, int i) {
        if (i==0) return dest.x=value;
        if (i==1) return dest.y=value;
        throw new IndexOutOfBoundsException();
    }
    public static void set(Vector2f dest, float[] dat, int from) {
        for (int i = 0;i < Vector2f.SIZE;i++) {
            Vector2f.set(dest, dat[from+i], i);
        }
    }
}
