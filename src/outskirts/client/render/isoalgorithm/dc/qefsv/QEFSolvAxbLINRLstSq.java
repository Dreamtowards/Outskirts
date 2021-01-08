package outskirts.client.render.isoalgorithm.dc.qefsv;

import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.mx.VertexUtil;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

import java.util.List;

public class QEFSolvAxbLINRLstSq {

    public static Vector3f solvLstSq(List<Vector3f> verts, List<Vector3f> norms) {
        Vector3f v = new Vector3f();
        try {
            solveLstSq(verts, norms, v);
        } catch (ArithmeticException ex) {
            // Zero det. Coplanar parallel. simpl do avg.
            Log.LOGGER.info("zero det. do avg.");
            VertexUtil.centeravg(verts, v);
        }
        return v;
    }

    private static Vector3f solveLstSq(List<Vector3f> verts, List<Vector3f> norms, Vector3f dest) {
        Matrix3f AtA = new Matrix3f();
        Vector3f Atb = new Vector3f();

        if (verts.size() == 3) {
            AtA.set(norms.get(0).x, norms.get(0).y, norms.get(0).z,
                    norms.get(1).x, norms.get(1).y, norms.get(1).z,
                    norms.get(2).x, norms.get(2).y, norms.get(2).z);
            Atb.set(Vector3f.dot(norms.get(0), verts.get(0)),
                    Vector3f.dot(norms.get(1), verts.get(1)),
                    Vector3f.dot(norms.get(2), verts.get(2)));
        } else {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    float f = 0;
                    for (int k = 0; k < verts.size(); k++) {
                        f += norms.get(k).get(i) * norms.get(k).get(j);
                    }
                    AtA.set(i, j, f);
                }
            }
            for (int i = 0; i < 3; i++) {
                float f = 0;
                for (int k = 0; k < verts.size(); k++) {
                    f += norms.get(k).get(i) * Vector3f.dot(verts.get(k), norms.get(k));
                }
                Vector3f.set(Atb, i, f);
            }
        }

        // do (AtA)^-1 *Atb
        if (Maths.fuzzyZero(AtA.determinant()))
            throw new ArithmeticException();
//        System.err.println(AtA.determinant());
        return Matrix3f.transform(AtA.invert(), dest.set(Atb));
    }

}
