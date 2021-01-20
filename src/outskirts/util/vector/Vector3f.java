package outskirts.util.vector;

import outskirts.util.Maths;
import outskirts.util.StringUtils;
import outskirts.util.function.IntFloatFunction;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
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

    public Vector3f(float[] v) { // shoud removed. or sync with 'set()'
        assert v.length == 3;
        set(v[0], v[1], v[2]);
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

    public Vector3f scale(float sx, float sy, float sz) {
        this.x *= sx;
        this.y *= sy;
        this.z *= sz;
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
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
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

    public static float dot(Vector3f a, Vector3f b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
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




    // ext
    public static float triprodu(Vector3f a, Vector3f b, Vector3f c) {
        return dot(a, cross(b, c, null));
    }

    //ext
    /**
     * calculate Normal Vetcor of the Triangle. not had normalized.
     * @param v1,v2,v3 the Triangle.
     * @param forcedir the angle between Triangle-Normal AND normdir, will keeps in <= 90'. (when > 90', just negated the norm.). Nullable
     */
    public static Vector3f trinorm(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f dest, Vector3f forcedir, Vector3f defnorm) {
        if (dest == null)
            dest = new Vector3f();
        Vector3f v1v2 = sub(v2, v1, null);
        Vector3f v1v3 = sub(v3, v1, null);
        cross(v1v2, v1v3, dest);
        if (dest.lengthSquared() == 0) {
            if (defnorm != null)
                return dest.set(defnorm);
            throw new ArithmeticException("not a really triangle. (point/line) ("+v1+", "+v2+", "+v3+")");
        }
        if (forcedir!=null && dot(dest, forcedir) < 0)
            dest.negate();
        return dest;
    }
    public static Vector3f trinorm(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f dest, Vector3f forcedir) {
        return trinorm(v1, v2, v3, dest, forcedir, null);
    }
    public static Vector3f trinorm(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f dest) {
        return trinorm(v1, v2, v3, dest, null, null);
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
//    //really should this..? (now for marchingCube lerp   Needs.
    public static Vector3f lerp(float t, Vector3f start, Vector3f end, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        return dest.set(
                Maths.lerp(t, start.x, end.x),
                Maths.lerp(t, start.y, end.y),
                Maths.lerp(t, start.z, end.z)
        );
    }

    public static Vector3f abs(Vector3f dest) {
        return dest.set(
                Math.abs(dest.x),
                Math.abs(dest.y),
                Math.abs(dest.z)
        );
    }

    public static void swap(Vector3f a, Vector3f b) {
        float ax=a.x, ay=a.y, az=a.z;
        a.set(b);
        b.set(ax, ay, az);
    }

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

    public static Vector3f floor(Vector3f dest, float u) {
        return dest.set(Maths.floor(dest.x, u),
                        Maths.floor(dest.y, u),
                        Maths.floor(dest.z, u));
    }


    public float get(int i) {
        if (i==0) return x;
        if (i==1) return y;
        if (i==2) return z;
        throw new IndexOutOfBoundsException();
    }
    public float setv(int i, float v) {  // dosent confuse with set(vec). v means scalar value.
        if (i==0) return x=v;
        if (i==1) return y=v;
        if (i==2) return z=v;
        throw new IndexOutOfBoundsException();
    }

    public float addv(int i, float v) {
        if (i==0) return x+=v;
        if (i==1) return y+=v;
        if (i==2) return z+=v;
        throw new IndexOutOfBoundsException();
    }

    public Vector3f add(float f) {
        return add(f, f, f);
    }
    public Vector3f sub(float f) {
        return sub(f, f, f);
    }

    public static Vector3f fromString(String s) {
        float[] f = StringUtils.readNumbers(s, new float[3]);
        return new Vector3f(f[0], f[1], f[2]);
    }

    public static final int SIZE = 3;  // "length" Misleading. "components" HighLv.d
    public static Vector3f set(Vector3f dest, IntFloatFunction getter, int from) {
        for (int i = 0;i < Vector3f.SIZE;i++) {
            dest.setv(i, getter.get(from+i));
        }
        return dest;
    }
    public static Vector3f set(Vector3f dest, float[] dat, int from) {
        return set(dest, i -> dat[i], from);
    }
    public static Vector3f set(Vector3f dest, float[] dat) {
        return set(dest, dat, 0);
    }


    // Vector3f.set(new Vector3f(), Vector.fromString("abc", new float[3]));
//    private static final Pattern SIMPLE_FLT_PATTERN = Pattern.compile("[-]?\\d+\\.\\d+");
//    // tmptool
//    // required one[, one]. example: ***[1.0, 2, 5f ]**
//    public static Vector3f fromString(String s, Vector3f dest) {
//        if (dest == null)
//            dest = new Vector3f();
//        Matcher m = SIMPLE_FLT_PATTERN.matcher(s);
//        for (int i = 0;i < Vector3f.SIZE;i++) {
//            if (!m.find()) {
//                throw new IllegalArgumentException("Illegal string. can not find 3 numbers.");
//            }
//            Vector3f.set(dest, i, Float.parseFloat(m.group()));
//        }
//        return dest;
//    }
}
