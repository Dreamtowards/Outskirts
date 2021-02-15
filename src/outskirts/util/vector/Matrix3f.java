package outskirts.util.vector;

import java.nio.FloatBuffer;

public class Matrix3f extends Matrix {

    public static final Matrix3f IDENTITY = new Matrix3f().setIdentity();

    /**
     * m00 m01 m02
     * m10 m11 m12
     * m20 m21 m22
     */
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;

    public Matrix3f() {
        setIdentity();
    }

    public Matrix3f(Matrix3f src) {
        set(src);
    }

    public Matrix3f(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        set(    m00, m01, m02,
                m10, m11, m12,
                m20, m21, m22);
    }

    public Matrix3f set(Matrix3f src) {
        return set(src.m00, src.m01, src.m02,
                   src.m10, src.m11, src.m12,
                   src.m20, src.m21, src.m22);
    }

    public Matrix3f set(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        this.m00 = m00; this.m01 = m01; this.m02 = m02;
        this.m10 = m10; this.m11 = m11; this.m12 = m12;
        this.m20 = m20; this.m21 = m21; this.m22 = m22;
        return this;
    }

    @Override
    public Matrix3f setIdentity() {
        return set(
                1, 0, 0,
                0, 1, 0,
                0, 0, 1
        );
    }

    @Override
    public Matrix3f setZero() {
        return set(
                0, 0, 0,
                0, 0, 0,
                0, 0, 0
        );
    }

    @Override
    public Matrix3f negate() {
        this.m00 = -this.m00; this.m01 = -this.m01; this.m02 = -this.m02;
        this.m10 = -this.m10; this.m11 = -this.m11; this.m12 = -this.m12;
        this.m20 = -this.m20; this.m21 = -this.m21; this.m22 = -this.m22;
        return this;
    }

    @Override
    public Matrix3f transpose() {
        return set(
                m00, m10, m20,
                m01, m11, m21,
                m02, m12, m22
        );
    }

    @Override
    public Matrix3f invert() {
        float det = determinant();
        if (det == 0.0f)
            throw new ArithmeticException("Zero determinant matrix.");

        float invdet = 1.0F / det;
        float t00 =  m11 * m22 - m21 * m12;
        float t01 = -m01 * m22 + m21 * m02;
        float t02 =  m01 * m12 - m11 * m02;
        float t10 = -m10 * m22 + m20 * m12;
        float t11 =  m00 * m22 - m20 * m02;
        float t12 = -m00 * m12 + m10 * m02;
        float t20 =  m10 * m21 - m20 * m11;
        float t21 = -m00 * m21 + m20 * m01;
        float t22 =  m00 * m11 - m10 * m01;

        return set(
                t00 * invdet, t01 * invdet, t02 * invdet,
                t10 * invdet, t11 * invdet, t12 * invdet,
                t20 * invdet, t21 * invdet, t22 * invdet
        );
    }

    @Override
    public float determinant() {
        return  m00 * (m11 * m22 - m12 * m21) -
                m01 * (m10 * m22 - m12 * m20) +
                m02 * (m10 * m21 - m11 * m20);
    }

    public static float determinant(float t00, float t01, float t02, float t10, float t11, float t12, float t20, float t21, float t22) {
        return t00 * (t11 * t22 - t12 * t21) - t01 * (t10 * t22 - t12 * t20) + t02 * (t10 * t21 - t11 * t20);
    }

    @Override
    public String toString() {
        return  m00 + " " + m01 + " " + m02 + "\n" +
                m10 + " " + m11 + " " + m12 + "\n" +
                m20 + " " + m21 + " " + m22 + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Matrix3f) {
            Matrix3f other = (Matrix3f) obj;
            return  other.m00 == this.m00 && other.m01 == this.m01 && other.m02 == this.m02 &&
                    other.m10 == this.m10 && other.m11 == this.m11 && other.m12 == this.m12 &&
                    other.m20 == this.m20 && other.m21 == this.m21 && other.m22 == this.m22;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        long hash = 0;
        hash = 31L * hash + Float.floatToIntBits(m00);
        hash = 31L * hash + Float.floatToIntBits(m01);
        hash = 31L * hash + Float.floatToIntBits(m02);
        hash = 31L * hash + Float.floatToIntBits(m10);
        hash = 31L * hash + Float.floatToIntBits(m11);
        hash = 31L * hash + Float.floatToIntBits(m12);
        hash = 31L * hash + Float.floatToIntBits(m20);
        hash = 31L * hash + Float.floatToIntBits(m21);
        hash = 31L * hash + Float.floatToIntBits(m22);
        return (int) (hash ^ hash >> 32);
    }

    public static float[] store(Matrix3f matrix, float[] buf) {
        buf[0]=matrix.m00; buf[1]=matrix.m01; buf[2]=matrix.m02;
        buf[3]=matrix.m10; buf[4]=matrix.m11; buf[5]=matrix.m12;
        buf[6]=matrix.m20; buf[7]=matrix.m21; buf[8]=matrix.m22;
        return buf;
    }

    public static void load(Matrix3f dest, float[] buf) {
        dest.m00 = buf[0];
        dest.m01 = buf[1];
        dest.m02 = buf[2];
        dest.m10 = buf[3];
        dest.m11 = buf[4];
        dest.m12 = buf[5];
        dest.m20 = buf[6];
        dest.m21 = buf[7];
        dest.m22 = buf[8];
    }

    public Matrix3f add(Matrix3f right) {
        return Matrix3f.add(this, right, this);
    }

    public Matrix3f sub(Matrix3f right) {
        return Matrix3f.sub(this, right, this);
    }

    public static Matrix3f add(Matrix3f left, Matrix3f right, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();

        dest.m00 = left.m00 + right.m00; dest.m01 = left.m01 + right.m01; dest.m02 = left.m02 + right.m02;
        dest.m10 = left.m10 + right.m10; dest.m11 = left.m11 + right.m11; dest.m12 = left.m12 + right.m12;
        dest.m20 = left.m20 + right.m20; dest.m21 = left.m21 + right.m21; dest.m22 = left.m22 + right.m22;

        return dest;
    }

    public static Matrix3f sub(Matrix3f left, Matrix3f right, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();

        dest.m00 = left.m00 - right.m00; dest.m01 = left.m01 - right.m01; dest.m02 = left.m02 - right.m02;
        dest.m10 = left.m10 - right.m10; dest.m11 = left.m11 - right.m11; dest.m12 = left.m12 - right.m12;
        dest.m20 = left.m20 - right.m20; dest.m21 = left.m21 - right.m21; dest.m22 = left.m22 - right.m22;

        return dest;
    }

    public static Matrix3f mul(Matrix3f left, Matrix3f right, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();

        float m00 = left.m00 * right.m00 + left.m01 * right.m10 + left.m02 * right.m20;
        float m01 = left.m00 * right.m01 + left.m01 * right.m11 + left.m02 * right.m21;
        float m02 = left.m00 * right.m02 + left.m01 * right.m12 + left.m02 * right.m22;
        float m10 = left.m10 * right.m00 + left.m11 * right.m10 + left.m12 * right.m20;
        float m11 = left.m10 * right.m01 + left.m11 * right.m11 + left.m12 * right.m21;
        float m12 = left.m10 * right.m02 + left.m11 * right.m12 + left.m12 * right.m22;
        float m20 = left.m20 * right.m00 + left.m21 * right.m10 + left.m22 * right.m20;
        float m21 = left.m20 * right.m01 + left.m21 * right.m11 + left.m22 * right.m21;
        float m22 = left.m20 * right.m02 + left.m21 * right.m12 + left.m22 * right.m22;

        return dest.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    public static Matrix3f mul(Matrix3f left, float r00, float r01, float r02,
                                              float r10, float r11, float r12,
                                              float r20, float r21, float r22, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();

        float m00 = left.m00 * r00 + left.m01 * r10 + left.m02 * r20;
        float m01 = left.m00 * r01 + left.m01 * r11 + left.m02 * r21;
        float m02 = left.m00 * r02 + left.m01 * r12 + left.m02 * r22;
        float m10 = left.m10 * r00 + left.m11 * r10 + left.m12 * r20;
        float m11 = left.m10 * r01 + left.m11 * r11 + left.m12 * r21;
        float m12 = left.m10 * r02 + left.m11 * r12 + left.m12 * r22;
        float m20 = left.m20 * r00 + left.m21 * r10 + left.m22 * r20;
        float m21 = left.m20 * r01 + left.m21 * r11 + left.m22 * r21;
        float m22 = left.m20 * r02 + left.m21 * r12 + left.m22 * r22;

        return dest.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    // todo: make "right" as dest ..? yes did now
    public static Vector3f transform(Matrix3f left, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();

        float x = left.m00 * dest.x + left.m01 * dest.y + left.m02 * dest.z;
        float y = left.m10 * dest.x + left.m11 * dest.y + left.m12 * dest.z;
        float z = left.m20 * dest.x + left.m21 * dest.y + left.m22 * dest.z;

        return dest.set(x, y, z);
    }

    // param order
    // ...[dest] last dest param order may Counterintuition in 2-params static-operation method
    // people will think the method is applies to first param, but actually applies in second param. first param actually just a arguments..
    // but there still ok. dest-lasting is the lib-style, unexcepted format will makes confuses. and dest was opt param.
    public static Matrix3f scale(Vector3f diag, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();

        dest.m00 *= diag.x; dest.m01 *= diag.y; dest.m02 *= diag.z;
        dest.m10 *= diag.x; dest.m11 *= diag.y; dest.m12 *= diag.z;
        dest.m20 *= diag.x; dest.m21 *= diag.y; dest.m22 *= diag.z;

        return dest;
    }

    // mat * rotate =  mat the rotate
    // rotate * mat = rotate the mat
    /**
     * note that there will NOT apply the rotation to the dest matrix. just gen a rotation matrix.
     * (because the apply of rotation should be a Explicit big operation. the multiplication order A*B or B*A will effects the result.
     * @param angle unit by radians
     * @param axis requires unit vector / normalized
     */
    public static Matrix3f rotate(float angle, Vector3f axis, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();

        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        float oneminusc = 1.0F - c;
        float xy = axis.x * axis.y;
        float yz = axis.y * axis.z;
        float xz = axis.x * axis.z;
        float xs = axis.x * s;
        float ys = axis.y * s;
        float zs = axis.z * s;

        float f00 = axis.x * axis.x * oneminusc + c;
        float f01 = xy * oneminusc - zs;
        float f02 = xz * oneminusc + ys;

        float f10 = xy * oneminusc + zs;
        float f11 = axis.y * axis.y * oneminusc + c;
        float f12 = yz * oneminusc - xs;

        float f20 = xz * oneminusc - ys;
        float f21 = yz * oneminusc + xs;
        float f22 = axis.z * axis.z * oneminusc + c;

//        return mul(dest, f00, f01, f02,
//                         f10, f11, f12,
//                         f20, f21, f22, dest);
        return dest.set(f00, f01, f02,
                        f10, f11, f12,
                        f20, f21, f22);
    }


    public float get(int r, int c) {
        if (r==0) {
            if (c==0) return m00;
            if (c==1) return m01;
            if (c==2) return m02;
        } else if (r==1) {
            if (c==0) return m10;
            if (c==1) return m11;
            if (c==2) return m12;
        } else if (r==2) {
            if (c==0) return m20;
            if (c==1) return m21;
            if (c==2) return m22;
        }
        throw new IndexOutOfBoundsException();
    }

    public float set(int r, int c, float v) {
        if (r==0) {
            if (c==0) return m00=v;
            if (c==1) return m01=v;
            if (c==2) return m02=v;
        } else if (r==1) {
            if (c==0) return m10=v;
            if (c==1) return m11=v;
            if (c==2) return m12=v;
        } else if (r==2) {
            if (c==0) return m20=v;
            if (c==1) return m21=v;
            if (c==2) return m22=v;
        }
        throw new IndexOutOfBoundsException();
    }


    public Matrix3f setSymmetric(float m00, float m01, float m02, float m11, float m12, float m22) {
        return set(
                m00, m01, m02,
                m01, m11, m12,
                m02, m12, m22
        );
    }


    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static Matrix3f set(Matrix3f dest, Matrix4f src) {
        if (dest == null)
            dest = new Matrix3f();
        for (int i = 0;i < Matrix3f.ROWS;i++) {
            for (int j = 0;j < Matrix3f.COLS;j++) {
                dest.set(i, j, src.get(i, j));
            }
        }
        return dest;
    }
}





