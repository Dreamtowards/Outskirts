package outskirts.client.render.isoalgorithm.dc.qefsv;

import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

import java.util.List;

public class QEFSolvL20 {

    public static void main(String[] args) {

        Log.LOGGER.info(doSLV(QEFSolvDCJAM3.TEST_Pi, QEFSolvDCJAM3.TEST_Ni));
    }

    public static Vector3f doSLV(List<Vector3f> vs, List<Vector3f> ns) {
        QEFSolvL20 q = new QEFSolvL20();

        for (int i = 0; i < vs.size(); i++) {
            q.Add(new Vector3f(vs.get(i)), new Vector3f(ns.get(i)));
        }

        float tol = Maths.FLT_EPSILON;
        return q.Solve(0.0001f, 50, 0.0001f);
    }

    public float ata_00, ata_01, ata_02, ata_11, ata_12, ata_22;
    public float atb_x, atb_y, atb_z;
    public float btb;
    public float massPoint_x, massPoint_y, massPoint_z;
    public int numPoints;

    public void Add(Vector3f p, Vector3f n)
    {
        n.normalize();
        ata_00 += n.x * n.x;
        ata_01 += n.x * n.y;
        ata_02 += n.x * n.z;
        ata_11 += n.y * n.y;
        ata_12 += n.y * n.z;
        ata_22 += n.z * n.z;
        float dot = Vector3f.dot(n,n);
        atb_x += dot * n.x;
        atb_y += dot * n.y;
        atb_z += dot * n.z;
        btb += dot * dot;
        massPoint_x += p.x;
        massPoint_y += p.y;
        massPoint_z += p.z;
        ++numPoints;
    }

    public Matrix3f ata = new Matrix3f();
    public Vector3f atb = new Vector3f(), x = new Vector3f();

    private void SetAta() {
        ata.setSymmetric(ata_00, ata_01, ata_02,
                                 ata_11, ata_12,
                                         ata_22);
    }

    private void SetAtb() {
        atb = new Vector3f(atb_x, atb_y, atb_z);
    }

    public Vector3f Solve(float svd_tol, int svd_sweeps, float pinv_tol)
    {
        if (numPoints == 0) throw new RuntimeException("...");

        Vector3f MassPoint = new Vector3f(massPoint_x, massPoint_y, massPoint_z);
        MassPoint.scale(1f / numPoints);

        this.SetAta();
        this.SetAtb();
        Vector3f tmpv = Matrix3f.transform(ata, new Vector3f(MassPoint));
        atb.sub(tmpv);
        x = new Vector3f();
        float result = SVD_SolveSymmetric(this.ata, this.atb, this.x, svd_tol, svd_sweeps, pinv_tol);
        //MassPoint /= (float)data.numPoints;
        if (Float.isNaN(result))
        {
            x = MassPoint;
            Log.LOGGER.info("NaN result.");
        }
        else
            x.add(MassPoint);
        float last_error = result;
        assert (result >= 0.0f);
        this.SetAtb();
        //output = x;
        //return result;

        return x;
//        return MassPoint;
    }

    public static float SVD_SolveSymmetric(Matrix3f A, Vector3f b, Vector3f x, float svd_tol, int svd_sweeps, float pinv_tol)
    {
        Matrix3f mtmp = new Matrix3f(), pinv, V = new Matrix3f();
        Matrix3f VTAV = new Matrix3f();
        GetSymmetricSvd( A, VTAV, V, svd_tol, svd_sweeps);
        pinv = PseudoInverse(VTAV, V, pinv_tol);
        x = Matrix3f.transform(pinv, new Vector3f(b));
        return CalcError(A, x, b);
    }

    public static void GetSymmetricSvd( Matrix3f a, Matrix3f vtav, Matrix3f v, float tol, int max_sweeps)
    {
        vtav.set(a);
        v.set(1, 0, 0, 0, 1, 0, 0, 0, 1);
        float delta = tol * Mat3Fnorm(vtav);

        for (int i = 0; i < max_sweeps && Mat3Off(vtav) > delta; i++)
        {
            Rotate01( vtav, v);
            Rotate02( vtav, v);
            Rotate12( vtav, v);
        }
    }

    // https://github.com/Lin20/isosurface/blob/master/Isosurface/Isosurface/QEFSolver/SMat3.cs
    private static float Mat3Fnorm(Matrix3f m) {
        return (float)Math.sqrt(
                  (m.m00 * m.m00) + (m.m01 * m.m01) + (m.m02 * m.m02)
                + (m.m10 * m.m10) + (m.m11 * m.m11) + (m.m12 * m.m12)
                + (m.m20 * m.m20) + (m.m21 * m.m21) + (m.m22 * m.m22));
    }
    public static float Mat3Off(Matrix3f m) {
        return (float)Math.sqrt(
                  (m.m01 * m.m01) + (m.m02 * m.m02) + (m.m10 * m.m10)
                + (m.m12 * m.m12) + (m.m20 * m.m20) + (m.m21 * m.m21));
    }

    public static void Rotate01( Matrix3f vtav, Matrix3f v) {
        if (vtav.m01 == 0) return;

        SM3Rot01(vtav);
        
        M3Rot01_post(v, 0, 0);
    }

    public static void Rotate02( Matrix3f vtav, Matrix3f v) {
        if (vtav.m02 == 0) return;

        SM3Rot02(vtav);

        M3Rot02_post(v, 0, 0);
    }
    public static void Rotate12( Matrix3f vtav, Matrix3f v) {
        if (vtav.m12 == 0) return;

        SM3Rot12(vtav);

        M3Rot12_post(v, 0, 0);
    }

    public static void SM3Rot01( Matrix3f m )
    {
        float[] cs = new float[2];
        Mat3CalcSymmetricGivensCoefficients(m.m00, m.m01, m.m11, cs);
        float c=cs[0], s=cs[1];
        float cc = c * c;
        float ss = s * s;
        float mix = 2 * c * s * m.m01;
        m.setSymmetric(cc * m.m00 - mix + ss * m.m11, 0, c * m.m02 - s * m.m12,
                ss * m.m00 + mix + cc * m.m11, s * m.m02 + c * m.m12, m.m22);
    }
    public static void SM3Rot02( Matrix3f m )
    {
        float[] cs = new float[2];
        Mat3CalcSymmetricGivensCoefficients(m.m00, m.m02, m.m22, cs);
        float c=cs[0], s=cs[1];
        float cc = c * c;
        float ss = s * s;
        float mix = 2 * c * s * m.m02;
        m.setSymmetric(cc * m.m00 - mix + ss * m.m22, c * m.m01 - s * m.m12, 0,
                m.m11, s * m.m01 + c * m.m12, ss * m.m00 + mix + cc * m.m22);
    }

    public static void SM3Rot12( Matrix3f m )
    {
        float[] cs = new float[2];
        Mat3CalcSymmetricGivensCoefficients(m.m11, m.m12, m.m22, cs);
        float c=cs[0], s=cs[1];
        float cc = c * c;
        float ss = s * s;
        float mix = 2 * c * s * m.m12;
        m.setSymmetric(m.m00, c * m.m01 - s * m.m02, s * m.m01 + c * m.m02,
                cc * m.m11 - mix + ss * m.m22, 0, ss * m.m11 + mix + cc * m.m22);
    }

    public static void Mat3CalcSymmetricGivensCoefficients(float a_pp, float a_pq, float a_qq, float[] cs)
    {
        if (a_pq == 0)
        {
            cs[0] = 1;
            cs[1] = 0;
            return;
        }

        float tau = (a_qq - a_pp) / (2.0f * a_pq);
        float stt = (float)Math.sqrt(1.0f + tau * tau);
        float tan = 1.0f / ((tau >= 0) ? (tau + stt) : (tau - stt));
        cs[0] = 1.0f / (float)Math.sqrt(1.0f + tan * tan);
        cs[1] = tan * cs[0];
    }

    public static void M3Rot01_post( Matrix3f m, float c, float s) {
        m.set(c * m.m00 - s * m.m01, s * m.m00 + c * m.m01, m.m02, c * m.m10 - s * m.m11,
                s * m.m10 + c * m.m11, m.m12, c * m.m20 - s * m.m21, s * m.m20 + c * m.m21, m.m22);
    }

    public static void M3Rot02_post( Matrix3f m, float c, float s) {
        m.set(c * m.m00 - s * m.m02, m.m01, s * m.m00 + c * m.m02, c * m.m10 - s * m.m12, m.m11,
                s * m.m10 + c * m.m12, c * m.m20 - s * m.m22, m.m21, s * m.m20 + c * m.m22);
    }

    public static void M3Rot12_post( Matrix3f m, float c, float s) {
        m.set(m.m00, c * m.m01 - s * m.m02, s * m.m01 + c * m.m02, m.m10, c * m.m11 - s * m.m12,
                s * m.m11 + c * m.m12, m.m20, c * m.m21 - s * m.m22, s * m.m21 + c * m.m22);
    }





    public static Matrix3f PseudoInverse( Matrix3f d, Matrix3f v, float tol)
    {
        Matrix3f m = new Matrix3f();
        float d0 = Pinv(d.m00, tol), d1 = Pinv(d.m11, tol), d2 = Pinv(d.m22,
                tol);
        m.set(v.m00 * d0 * v.m00 + v.m01 * d1 * v.m01 + v.m02 * d2 * v.m02,
                v.m00 * d0 * v.m10 + v.m01 * d1 * v.m11 + v.m02 * d2 * v.m12,
                v.m00 * d0 * v.m20 + v.m01 * d1 * v.m21 + v.m02 * d2 * v.m22,
                v.m10 * d0 * v.m00 + v.m11 * d1 * v.m01 + v.m12 * d2 * v.m02,
                v.m10 * d0 * v.m10 + v.m11 * d1 * v.m11 + v.m12 * d2 * v.m12,
                v.m10 * d0 * v.m20 + v.m11 * d1 * v.m21 + v.m12 * d2 * v.m22,
                v.m20 * d0 * v.m00 + v.m21 * d1 * v.m01 + v.m22 * d2 * v.m02,
                v.m20 * d0 * v.m10 + v.m21 * d1 * v.m11 + v.m22 * d2 * v.m12,
                v.m20 * d0 * v.m20 + v.m21 * d1 * v.m21 + v.m22 * d2 * v.m22);

        return m;
    }

    public static float Pinv(float x, float tol)
    {
        return ((float)Math.abs(x) < tol || (float)Math.abs(1.0f / x) < tol) ? 0 : (1 / x);
    }


    public static float CalcError(Matrix3f a, Vector3f x, Vector3f b)
    {
        Vector3f vtmp = Matrix3f.transform(a, new Vector3f(x));
        vtmp = Vector3f.sub(b, vtmp, null);
        return Vector3f.dot(vtmp, vtmp);
    }

}
