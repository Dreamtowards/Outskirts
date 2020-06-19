package outskirts.util.vector;

import outskirts.util.Maths;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vector3f extends Vector {

    public static final Vector3f ZERO = new Vector3f(0.0f, 0.0f, 0.0f);   //default 'value' like required-position..
    public static final Vector3f ONE  = new Vector3f(1.0f, 1.0f, 1.0f);   //default scale

    public static final Vector3f UNIT_X = new Vector3f(1.0f, 0.0f, 0.0f); //rotation axis
    public static final Vector3f UNIT_Y = new Vector3f(0.0f, 1.0f, 0.0f);
    public static final Vector3f UNIT_Z = new Vector3f(0.0f, 0.0f, 1.0f);

    public float x;
    public float y;
    public float z;

    public Vector3f() {}

    public Vector3f(Vector3f src) {
        set(src);
    }

    public Vector3f(float x, float y, float z) {
        set(x, y, z);
    }

    public Vector3f set(Vector3f src) {
        return set(src.x, src.y, src.z);
    }

    public Vector3f set(float x, float y, float z) {
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
    public Vector3f scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    @Override
    public Vector3f negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    @Override
    public Vector3f normalize() {
        return (Vector3f) Vector.normalize(this);
    }

    @Override
    public String toString() {
        return "Vector3f[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector3f && ((Vector3f) obj).x == this.x && ((Vector3f) obj).y == this.y && ((Vector3f) obj).z == this.z;
    }

    @Override
    public int hashCode() {
        long hash = 0;
        hash = 31L * hash + Float.floatToIntBits(this.x);
        hash = 31L * hash + Float.floatToIntBits(this.y);
        hash = 31L * hash + Float.floatToIntBits(this.z);
        return (int) (hash ^ hash >> 32);
    }

    public Vector3f setX(float x) {
        this.x = x;
        return this;
    }
    public Vector3f setY(float y) {
        this.y = y;
        return this;
    }
    public Vector3f setZ(float z) {
        this.z = z;
        return this;
    }

    public Vector3f add(Vector3f right) {
        return add(right.x, right.y, right.z);
    }
    public Vector3f add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public final Vector3f sub(Vector3f right) {
        return add(-right.x, -right.y, -right.z);
    }
    public final Vector3f sub(float x, float y, float z) {
        return add(-x, -y, -z);
    }

    public static Vector3f add(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        return dest.set(
                left.x + right.x,
                left.y + right.y,
                left.z + right.z
        );
    }

    public static Vector3f sub(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        return dest.set(
                left.x - right.x,
                left.y - right.y,
                left.z - right.z
        );
    }

    public static float dot(Vector3f left, Vector3f right) {
        return left.x * right.x + left.y * right.y + left.z * right.z;
    }

    public static float angle(Vector3f a, Vector3f b) {
        return Vector.angle(dot(a, b), a, b);
    }

    public static Vector3f cross(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        return dest.set(
                left.y * right.z - left.z * right.y,
                left.z * right.x - left.x * right.z,
                left.x * right.y - left.y * right.x
        );
    }





    //ext
    /**
     * calculate Normal Vetcor of the Triangle. not had normalized.
     * @param v1,v2,v3 the Triangle.
     * @param normdir the angle between Triangle-Normal AND normdir, will keeps in <= 90'. (when > 90', just negated the norm.). Nullable
     */
    public static Vector3f trinorm(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f normdir, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        Vector3f v1v2 = sub(v2, v1, null);
        Vector3f v1v3 = sub(v3, v1, null);
        cross(v1v2, v1v3, dest);
        if (dest.lengthSquared() == 0)
            throw new IllegalArgumentException("not a really triangle. (point/line)");
        if (normdir!=null && dot(dest, normdir) < 0)
            dest.negate();
        return dest;
    }

    //ext /order: num then vec because num simple, being a lightweight premise, and focus most in the latter vector
    public Vector3f addScaled(float scalar, Vector3f factor) {
        this.x += factor.x * scalar;
        this.y += factor.y * scalar;
        this.z += factor.z * scalar;
        return this;
    }

//    //should this..? (now for block pos select, get chunkpos by worldpos.
//    public static Vector3f floor(Vector3f vec, float unitsize) {
//        return vec.set(
//                Maths.floor(vec.x, unitsize),
//                Maths.floor(vec.y, unitsize),
//                Maths.floor(vec.z, unitsize)
//        );
//    }
//
//    //really should this..? (now for marchingCube lerp
//    public static Vector3f lerp(float t, Vector3f start, Vector3f end, Vector3f dest) {
//        if (dest == null)
//            dest = new Vector3f();
//        return dest.set(
//                Maths.lerp(t, start.x, end.x),
//                Maths.lerp(t, start.y, end.y),
//                Maths.lerp(t, start.z, end.z)
//        );
//    }

    /**
     * @param norm normalized normal vector
     * @param dest src vector and will be as output reference
     */
    public static Vector3f reflect(Vector3f norm, Vector3f dest) {
        float projlen2 = -Vector3f.dot(norm, dest) * 2f;
        return dest.set(
                dest.x + projlen2*norm.x,
                dest.y + projlen2*norm.y,
                dest.z + projlen2*norm.z
        );
    }

    public static final int SIZE = 3;  // "length" Misleading. "components" HighLv.d
    public static float get(Vector3f src, int i) {
        if (i==0) return src.x;
        if (i==1) return src.y;
        if (i==2) return src.z;
        throw new IndexOutOfBoundsException();
    }
    public static float set(Vector3f dest, int i, float value) { // todo: return "dest" or setted-value ...?
        if (i==0) return dest.x=value;
        if (i==1) return dest.y=value;
        if (i==2) return dest.z=value;
        throw new IndexOutOfBoundsException();
    }
    public static Vector3f set(Vector3f dest, float[] dat, int from) {
        for (int i = 0;i < Vector3f.SIZE;i++) {
            Vector3f.set(dest, i, dat[from+i]);
        }
        return dest;
    }


    private static final Pattern SIMPLE_FLT_PATTERN = Pattern.compile("[+-]?\\d+\\.\\d+");
    // tmptool
    // required one[, one]. example: ***[1.0, 2, 5f ]**
    public static Vector3f fromString(String s, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        Matcher m = SIMPLE_FLT_PATTERN.matcher(s);
        for (int i = 0;i < Vector3f.SIZE;i++) {
            if (!m.find()) {
                throw new IllegalArgumentException("Illegal string. can not find 3 numbers.");
            }
            Vector3f.set(dest, i, Float.parseFloat(m.group()));
        }
        return dest;
    }
}
