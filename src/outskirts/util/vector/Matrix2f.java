package outskirts.util.vector;

import java.nio.FloatBuffer;

public class Matrix2f extends Matrix {

    public static final Matrix2f IDENTITY = new Matrix2f().setIdentity();

    /**
     * m00 m01
     * m10 m11
     */
    public float m00;
    public float m01;
    public float m10;
    public float m11;

    public Matrix2f() {
        setIdentity();
    }

    public Matrix2f(Matrix2f src) {
        set(src);
    }

    public Matrix2f(float m00, float m01, float m10, float m11) {
        set(m00, m01, m10, m11);
    }

    public Matrix2f set(Matrix2f src) {
        return set(src.m00, src.m01, src.m10, src.m11);
    }

    public Matrix2f set(float m00, float m01, float m10, float m11) {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
        return this;
    }

    @Override
    public Matrix2f setIdentity() {
        return set(
                1, 0,
                0, 1
        );
    }

    @Override
    public Matrix2f setZero() {
        return set(0, 0, 0, 0);
    }

    @Override
    public Matrix2f negate() {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m10 = -this.m10;
        this.m11 = -this.m11;
        return this;
    }

    @Override
    public Matrix2f transpose() {
        return set(
                m00, m10,
                m01, m11
        );
    }

    @Override
    public Matrix2f invert() {
        float det = determinant();
        if (det == 0.0f)
            throw new ArithmeticException("Zero determinant matrix.");

        float invdet = 1.0f / det;

        return set(
                m11 * invdet, -m01 * invdet,
                -m10 * invdet, m00 * invdet
        );
    }

    @Override
    public float determinant() {
        return m00 * m11 - m01 * m10;
    }

    @Override
    public String toString() {
        return  m00 + " " + m01 + "\n" +
                m10 + " " + m11 + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Matrix2f) {
            Matrix2f other = (Matrix2f) obj;
            return  other.m00 == this.m00 && other.m01 == this.m01 &&
                    other.m10 == this.m10 && other.m11 == this.m11;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        long hash = 0;
        hash = 31L * hash + Float.floatToIntBits(m00);
        hash = 31L * hash + Float.floatToIntBits(m01);
        hash = 31L * hash + Float.floatToIntBits(m10);
        hash = 31L * hash + Float.floatToIntBits(m11);
        return (int) (hash ^ hash >> 32);
    }

    public static float[] store(Matrix2f matrix, float[] buf) {
        buf[0]=matrix.m00; buf[1]=matrix.m01;
        buf[2]=matrix.m10; buf[3]=matrix.m11;
        return buf;
    }

    public static void load(Matrix2f dest, float[] buf) {
        buf[0] = dest.m00;
        buf[1] = dest.m01;
        buf[2] = dest.m10;
        buf[3] = dest.m11;
    }

    public Matrix2f add(Matrix2f right) {
        return Matrix2f.add(this, right, this);
    }

    public Matrix2f sub(Matrix2f right) {
        return Matrix2f.sub(this, right, this);
    }

    public static Matrix2f add(Matrix2f left, Matrix2f right, Matrix2f dest) {
        if (dest == null)
            dest = new Matrix2f();

        dest.m00 = left.m00 + right.m00;
        dest.m01 = left.m01 + right.m01;
        dest.m10 = left.m10 + right.m10;
        dest.m11 = left.m11 + right.m11;

        return dest;
    }

    public static Matrix2f sub(Matrix2f left, Matrix2f right, Matrix2f dest) {
        if (dest == null)
            dest = new Matrix2f();

        dest.m00 = left.m00 - right.m00;
        dest.m01 = left.m01 - right.m01;
        dest.m10 = left.m10 - right.m10;
        dest.m11 = left.m11 - right.m11;

        return dest;
    }

    public static Matrix2f mul(Matrix2f left, Matrix2f right, Matrix2f dest) {
        if (dest == null)
            dest = new Matrix2f();

        float m00 = left.m00 * right.m00 + left.m01 * right.m10;
        float m01 = left.m00 * right.m01 + left.m01 * right.m11;
        float m10 = left.m10 * right.m00 + left.m11 * right.m10;
        float m11 = left.m10 * right.m01 + left.m11 * right.m11;

        return dest.set(m00, m01, m10, m11);
    }

    public static Matrix2f mul(Matrix2f left, float r00, float r01,
                                              float r10, float r11, Matrix2f dest) {
        if (dest == null)
            dest = new Matrix2f();

        float m00 = left.m00 * r00 + left.m01 * r10;
        float m01 = left.m00 * r01 + left.m01 * r11;
        float m10 = left.m10 * r00 + left.m11 * r10;
        float m11 = left.m10 * r01 + left.m11 * r11;

        return dest.set(m00, m01, m10, m11);
    }

    public static Vector2f transform(Matrix2f left, Vector2f dest) {
        if (dest == null)
            dest = new Vector2f();

        float x = left.m00 * dest.x + left.m01 * dest.y;
        float y = left.m10 * dest.x + left.m11 * dest.y;

        return dest.set(x, y);
    }


    public static Matrix2f rotate(float angle, Matrix2f dest) {
        if (dest == null)
            dest = new Matrix2f();

        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);

        float f00 = c, f01 = -s;
        float f10 = s, f11 = c;

        return mul(dest, f00, f01, f10, f11, dest);
    }
}
