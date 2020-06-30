package outskirts.util.vector;

public class Quaternion extends Vector4f {

    public static final Quaternion IDENTITY = new Quaternion().setIdentity();

    public Quaternion() {
        setIdentity();
    }

    public Quaternion(Quaternion src) {
        set(src);
    }

    public Quaternion(float x, float y, float z, float w) {
        set(x, y, z, w);
    }

    @Override
    public Quaternion set(Vector4f src) { //should Quaternion only..?
        return set(src.x, src.y, src.z, src.w);
    }

    @Override
    public Quaternion set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    @Override
    public Quaternion scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }

    @Override
    public Quaternion negate() { // different with conjugate. this negate dose makes rotate flip!!
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    public Quaternion conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        // not w
        return this;
    }

    @Override
    public Quaternion normalize() {
        return (Quaternion) Vector.normalize(this);
    }

    @Override
    public String toString() {
        return "Quaternion[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Quaternion && ((Quaternion) obj).x == this.x && ((Quaternion) obj).y == this.y && ((Quaternion) obj).z == this.z && ((Quaternion) obj).w == this.w;
    }

    @Override
    public Quaternion setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public Quaternion setY(float y) {
        this.y = y;
        return this;
    }

    @Override
    public Quaternion setZ(float z) {
        this.z = z;
        return this;
    }

    @Override
    public Quaternion setW(float w) {
        this.w = w;
        return this;
    }

    @Override
    public Quaternion add(Vector4f right) {
        return add(right.x, right.y, right.z, right.w);
    }

    @Override
    public Quaternion add(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    @Override
    public Quaternion sub(Vector4f right) {
        return sub(right.x, right.y, right.z, right.w);
    }

    @Override
    public Quaternion sub(float x, float y, float z, float w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    public Quaternion setIdentity() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
        this.w = 1f;
        return this;
    }

    public Quaternion invert() {
        float invSq = 1.0F / lengthSquared();
        this.x *= -invSq;
        this.y *= -invSq;
        this.z *= -invSq;
        this.w *= invSq;
        return this;
    }

    public static Quaternion mul(Quaternion left, Quaternion right, Quaternion dest) {
        if (dest == null)
            dest = new Quaternion();
        return dest.set(
                left.x * right.w + left.w * right.x + left.y * right.z - left.z * right.y,
                left.y * right.w + left.w * right.y + left.z * right.x - left.x * right.z,
                left.z * right.w + left.w * right.z + left.x * right.y - left.y * right.x,
                left.w * right.w - left.x * right.x - left.y * right.y - left.z * right.z
        );
    }



    // EXTRA CONVERT FUNCTION

    public static Quaternion fromAxisAngle(Vector4f a, Quaternion dest) {
        return fromAxisAngle(a.x, a.y, a.z, a.w, dest);
    }

    public static Quaternion fromAxisAngle(Vector3f axis, float angle, Quaternion dest) {
        return fromAxisAngle(axis.x, axis.y, axis.z, angle, dest);
    }

    /**
     * q = [sin(½theta)d, cos(½theta)]
     * @param ax,ay,az axis xyz vector, requires unit-vector
     */
    private static Quaternion fromAxisAngle(float ax, float ay, float az, float angle, Quaternion dest) {
        if (dest == null)
            dest = new Quaternion();
        float s = (float)Math.sin(angle * 0.5f);
        return dest.set(
                ax * s,
                ay * s,
                az * s,
                (float)Math.cos(angle * 0.5f)
        );
    }

    /**
     * @param q required normalized src quaternion.
     */
    public static Vector4f toAxisAngle(Quaternion q, Vector4f dest) {
        if (dest == null)
            dest = new Vector4f();
        float s = (float)Math.sqrt(1f - q.w * q.w);
        if (s == 0)
            throw new ArithmeticException("Identity quaternion.");
        return dest.set(
                q.x / s,
                q.y / s,
                q.z / s,
                (float)Math.acos(q.w) * 2f
        );
    }

    public static Quaternion fromMatrix(Matrix3f mat, Quaternion dest) {
        return fromMatrix(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22, dest);
    }
    public static Quaternion fromMatrix(Matrix4f mat, Quaternion dest) {
        return fromMatrix(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22, dest);
    }

    /**
     * just like Matrix3f
     * m00 m01 m02
     * m10 m11 m12
     * m20 m21 m22
     */
    private static Quaternion fromMatrix(float m00, float m01, float m02,
                                         float m10, float m11, float m12,
                                         float m20, float m21, float m22, Quaternion dest) {
        if (dest == null)
            dest = new Quaternion();

        float tr = m00 + m11 + m22;
        float s;
        if (tr >= 0f) {
            s = (float) Math.sqrt(tr + 1f);
            dest.w = s * 0.5f;
            s = 0.5f / s;
            dest.x = (m21 - m12) * s;
            dest.y = (m02 - m20) * s;
            dest.z = (m10 - m01) * s;
        } else {
            float max = Math.max(Math.max(m00, m11), m22);
            if (max == m00) {
                s = (float) Math.sqrt(m00 - (m11 + m22) + 1f);
                dest.x = s * 0.5f;
                s = 0.5f / s;
                dest.y = (m01 + m10) * s;
                dest.z = (m20 + m02) * s;
                dest.w = (m21 - m12) * s;
            } else if (max == m11) {
                s = (float) Math.sqrt(m11 - (m22 + m00) + 1f);
                dest.y = s * 0.5f;
                s = 0.5f / s;
                dest.z = (m12 + m21) * s;
                dest.x = (m01 + m10) * s;
                dest.w = (m02 - m20) * s;
            } else {
                s = (float) Math.sqrt(m22 - (m00 + m11) + 1f);
                dest.z = s * 0.5f;
                s = 0.5f / s;
                dest.x = (m20 + m02) * s;
                dest.y = (m12 + m21) * s;
                dest.w = (m10 - m01) * s;
            }
        }
        return dest;
    }

    public static Matrix3f toMatrix(Quaternion q, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();
        float d = q.lengthSquared();
        if (d == 0.0F)
            throw new ArithmeticException("Zero length quaternion.");
        float s = 2f / d;
        float xs = q.x * s, ys = q.y * s, zs = q.z * s;
        float wx = q.w * xs, wy = q.w * ys, wz = q.w * zs;
        float xx = q.x * xs, xy = q.x * ys, xz = q.x * zs;
        float yy = q.y * ys, yz = q.y * zs, zz = q.z * zs;
        dest.m00 = 1f - (yy + zz);
        dest.m01 = xy - wz;
        dest.m02 = xz + wy;
        dest.m10 = xy + wz;
        dest.m11 = 1f - (xx + zz);
        dest.m12 = yz - wx;
        dest.m20 = xz - wy;
        dest.m21 = yz + wx;
        dest.m22 = 1f - (xx + yy);
        return dest;
    }

    // #duplicated_from Quaternion.toMatrix(Quaternion, Matrix3f)
    /**
     * note that this will only directly set(not mul) Matrix4x4's first 0-2 rows AND cols (9 elements), other elements will not be touch.
     */
    public static Matrix4f toMatrix(Quaternion q, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();
        float d = q.lengthSquared();
        if (d == 0.0F)
            throw new ArithmeticException("Zero length quaternion.");
        float s = 2f / d;
        float xs = q.x * s, ys = q.y * s, zs = q.z * s;
        float wx = q.w * xs, wy = q.w * ys, wz = q.w * zs;
        float xx = q.x * xs, xy = q.x * ys, xz = q.x * zs;
        float yy = q.y * ys, yz = q.y * zs, zz = q.z * zs;
        dest.m00 = 1f - (yy + zz);
        dest.m01 = xy - wz;
        dest.m02 = xz + wy;
        dest.m10 = xy + wz;
        dest.m11 = 1f - (xx + zz);
        dest.m12 = yz - wx;
        dest.m20 = xz - wy;
        dest.m21 = yz + wx;
        dest.m22 = 1f - (xx + yy);
        return dest;
    }






    public static Quaternion nlerp(float t, Quaternion start, Quaternion end, Quaternion dest) {
        return (Quaternion)Vector4f.lerp(t, start, end, dest).normalize();
    }

    // for anim interpolate
    public static Quaternion nlerpsf(float t, Quaternion start, Quaternion end, Quaternion dest) {
        if (dest == null)
            dest = new Quaternion();
        float dot = Quaternion.dot(start, end);
        if (dot < 0f)
            end.negate();
        Quaternion.nlerp(t, start, end, dest);
        if (dot < 0f)
            end.negate();  // negaet back.
        return dest;
    }
}