package outskirts.client.render.isoalgorithm.dc.qefsv;

import outskirts.util.Val;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.List;

// https://github.com/nickgildea/qef/blob/master/qef.cl
public class QEFSolvNKG {

    public static void main(String[] args) {


        Log.LOGGER.info(
                doSlv(QEFSolvDCJAM3.TEST_Pi, QEFSolvDCJAM3.TEST_Ni)
        );

    }

    public static Vector3f doSlv(List<Vector3f> vts, List<Vector3f> nms) {
        Vector4f v = new QEFSolvNKG().qef_solve_from_points(vts, nms, Val.zero());
        return new Vector3f(v.x, v.y, v.z);
    }

    Vector4f qef_solve_from_points(List<Vector3f> positions, List<Vector3f> normals, Val error) {
        Vector4f pointaccum = new Vector4f(0.f, 0.f, 0.f, 0.f);
        Vector4f ATb = new Vector4f(0.f, 0.f, 0.f, 0.f);
        Matrix3f ATA = new Matrix3f().setSymmetric(0.f, 0.f, 0.f, 0.f, 0.f, 0.f);

        for (int i= 0; i < positions.size(); ++i) {
            qef_add(normals.get(i),positions.get(i),ATA,ATb,pointaccum);
        }

        Vector4f solved_position = new Vector4f( );

        error.val = qef_solve(ATA,ATb,pointaccum, solved_position);
        return solved_position;
    }

    void qef_add( Vector3f n, Vector3f p, Matrix3f ATA, Vector4f ATb, Vector4f pointaccum)
    {
        ATA.m00 += n.x * n.x;
        ATA.m01 += n.x * n.y;
        ATA.m02 += n.x * n.z;
        ATA.m11 += n.y * n.y;
        ATA.m12 += n.y * n.z;
        ATA.m22 += n.z * n.z;

        float b = Vector3f.dot(p, n);
        (ATb).x += n.x * b;
        (ATb).y += n.y * b;
        (ATb).z += n.z * b;

        (pointaccum).x += p.x;
        (pointaccum).y += p.y;
        (pointaccum).z += p.z;
        (pointaccum).w += 1.f;
    }

    float qef_solve(Matrix3f ATA, Vector4f ATb, Vector4f pointaccum, Vector4f x) {
        Vector4f masspoint = pointaccum.scale(1f / pointaccum.w);

        Vector4f A_mp = new Vector4f( 0.f, 0.f, 0.f, 0.f );
        svd_vmul_sym(A_mp, ATA, masspoint);
        A_mp = Vector4f.sub(ATb, A_mp, null);

        svd_solve_ATA_ATb(ATA, A_mp, x);

        float error = qef_calc_error(ATA, x, ATb);
        x.add(masspoint);

        return error;
    }

    float qef_calc_error(Matrix3f A, Vector4f x, Vector4f b) {
        Vector4f tmp = new Vector4f();

        svd_vmul_sym(tmp, A, x);
        tmp = Vector4f.sub(b, tmp, null);

        return Vector4f.dot(tmp, tmp);
    }




    void svd_vmul_sym(Vector4f result, Matrix3f A, Vector4f v) {
        Vector4f A_row_x = new Vector4f( A.m00, A.m01, A.m02, 0.f );

        (result).x = Vector4f.dot(A_row_x, v);
        (result).y = A.m01 * v.x + A.m11 * v.y + A.m12 * v.z;
        (result).z = A.m02 * v.x + A.m12 * v.y + A.m22 * v.z;
    }

    void svd_solve_ATA_ATb(Matrix3f ATA, Vector4f ATb, Vector4f x)
    {
        Matrix3f V = new Matrix3f();

        Vector4f sigma = new Vector4f();
        svd_solve_sym(ATA, sigma, V);

        // A = UEV^T; U = A / (E*V^T)
        Matrix3f Vinv = new Matrix3f();
        svd_pseudoinverse(Vinv, sigma, V);
        svd_mul_matrix_vec(x, Vinv, ATb);
    }

    void svd_solve_sym(Matrix3f a, Vector4f sigma, Matrix3f v) { int SVD_NUM_SWEEPS=5;
        // assuming that A is symmetric: can optimize all operations for
        // the upper right triagonal
        Matrix3f vtav = new Matrix3f(a);

        // assuming V is identity: you can also pass a matrix the rotations
        // should be applied to. (U is not computed)
        for (int i = 0; i < SVD_NUM_SWEEPS; ++i) {
            svd_rotate(vtav, v, 0, 1);
            svd_rotate(vtav, v, 0, 2);
            svd_rotate(vtav, v, 1, 2);
        }

	    sigma.set(vtav.m00, vtav.m11, vtav.m22, 0.f);
    }

    float svd_invdet(float x, float tol) {
        return (Math.abs(x) < tol || Math.abs(1.0f / x) < tol) ? 0.0f : (1.0f / x);
    }

    void svd_pseudoinverse(Matrix3f o, Vector4f sigma, Matrix3f v) { float PSUEDO_INVERSE_THRESHOLD=0.1f;
        float d0 = svd_invdet(sigma.x, PSUEDO_INVERSE_THRESHOLD);
        float d1 = svd_invdet(sigma.y, PSUEDO_INVERSE_THRESHOLD);
        float d2 = svd_invdet(sigma.z, PSUEDO_INVERSE_THRESHOLD);

        o.m00 = v.m00 * d0 * v.m00 + v.m01 * d1 * v.m01 + v.m02 * d2 * v.m02;
        o.m01 = v.m00 * d0 * v.m10 + v.m01 * d1 * v.m11 + v.m02 * d2 * v.m12;
        o.m02 = v.m00 * d0 * v.m20 + v.m01 * d1 * v.m21 + v.m02 * d2 * v.m22;
        o.m10 = v.m10 * d0 * v.m00 + v.m11 * d1 * v.m01 + v.m12 * d2 * v.m02;
        o.m11 = v.m10 * d0 * v.m10 + v.m11 * d1 * v.m11 + v.m12 * d2 * v.m12;
        o.m12 = v.m10 * d0 * v.m20 + v.m11 * d1 * v.m21 + v.m12 * d2 * v.m22;
        o.m20 = v.m20 * d0 * v.m00 + v.m21 * d1 * v.m01 + v.m22 * d2 * v.m02;
        o.m21 = v.m20 * d0 * v.m10 + v.m21 * d1 * v.m11 + v.m22 * d2 * v.m12;
        o.m22 = v.m20 * d0 * v.m20 + v.m21 * d1 * v.m21 + v.m22 * d2 * v.m22;
    }


    void svd_rotate(Matrix3f vtav, Matrix3f v, int a, int b) {
        if (vtav.get(a, b) == 0.0) return;

        float[] cs = new float[2];
        givens_coeffs_sym(vtav.get(a,a), vtav.get(a,b), vtav.get(b,b), cs);

        {float[] xyz = new float[3];
        xyz[0] = vtav.get(a,a); xyz[1] = vtav.get(b,b); xyz[2] = vtav.get(a,b);
        svd_rotateq_xy(xyz, cs[0], cs[1]);
        vtav.set(a,a,xyz[0]); vtav.set(b,b,xyz[1]); vtav.set(a,b,xyz[2]);}

        {float[] xy = {vtav.get(0,3-b), vtav.get(1-a,2)};
        svd_rotate_xy(xy, cs[0], cs[1]);
        vtav.set(0,3-b,xy[0]); vtav.set(1-a,2,xy[1]);}

        vtav.set(a,b,0);

        float[] xy = new float[2];
        xy[0] = v.get(0,a); xy[1] = v.get(0,b);
        svd_rotate_xy(xy, cs[0], cs[1]);
        v.set(0,a,xy[0]); v.set(0,b,xy[1]);

        xy[0] = v.get(1,a); xy[1] = v.get(1,b);
        svd_rotate_xy(xy, cs[0], cs[1]);
        v.set(1,a, xy[0]); v.set(1,b,xy[1]);

        xy[0] = v.get(2,a); xy[1] = v.get(2,b);
        svd_rotate_xy(xy, cs[0], cs[1]);
        v.set(2,a,xy[0]); v.set(2,b,xy[1]);
    }

    void givens_coeffs_sym(float a_pp, float a_pq, float a_qq, float[] cs) {
        if (a_pq == 0.f) {
            cs[0] = 1.f;
            cs[1] = 0.f;
            return;
        }
        float tau = (a_qq - a_pp) / (2.f * a_pq);
        float stt = (float) Math.sqrt(1.f + tau * tau);
        float tan = 1.f / ((tau >= 0.f) ? (tau + stt) : (tau - stt));
        cs[0] = (float)Math.sqrt(1.f + tan * tan);
        cs[1] = tan * cs[0];
    }

    void svd_rotate_xy(float[] xy, float c, float s) {
        float u = xy[0]; float v = xy[1];
        xy[0] = c * u - s * v;
        xy[1] = s * u + c * v;
    }

    void svd_rotateq_xy(float[] xya, float c, float s) {
        float cc = c * c; float ss = s * s;
        float mx = 2.0f * c * s * (xya[2]);
        float u = xya[0]; float v = xya[1];
        xya[0] = cc * u - mx + ss * v;
        xya[1] = ss * u + mx + cc * v;
    }

    void svd_mul_matrix_vec(Vector4f result, Matrix3f a, Vector4f b)
    {
        (result).x = Vector4f.dot(new Vector4f(a.m00, a.m01, a.m02, 0.f), b);
        (result).y = Vector4f.dot(new Vector4f(a.m10, a.m11, a.m12, 0.f), b);
        (result).z = Vector4f.dot(new Vector4f(a.m20, a.m21, a.m22, 0.f), b);
        (result).w = 0.f;
    }
}
