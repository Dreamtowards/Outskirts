package outskirts.client.render.isoalgorithm.dc.qefsv;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.Maths;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;
import outskirts.world.gen.NoiseGeneratorPerlin;

import java.util.*;

/**
 *  Dual Contouring of Hermite Data.
 *
 * 1. Requirements.
 *    f(v), but also needs its gradient information. which f'(v) -> vec.
 *    (actually the gradient function f'(v) can be approximately obtain by diff the f(v).
 *
 * 2. Compute the Cell Interior Feature Vertex.
 *    For each cell, for each 12edges, find the edges which had a sign-change.                            PREPARE
 *    when found 0, the cell have not the feature vertex. else we may found [3-12] sign-change edges.
 *    get the intersect point and normal which the Hermite data of each edge.
 *
 *    Compute the cell interior feature vertex x by those points v_i and normals n_i.                     COMPUTE
 *    which minimize distanceSq of x with planes v_i n_i
 *      E(x) = SUM (n_i · (x - v_i))^2
 *    by Quadratic Error Function. QEF. use of Least Square method.
 *                 (n_i·x - n_i·v_i)^2
 *                 (Ax-b)^2                            lin.
 *                 (Ax-b)^T (Ax-b)                     Ax=b
 *                 xt(AtA)x - 2xt(Atb) + btb           AtAx=Atb
 *                 x=(AtA)^-1 Atb  // E d -> 0         x=(AtA)^-1 Atb
 *
 * 3. Connect Vertices for Build Mesh.
 *    For each edge which had sign-change,
 *    for the 4 cells adjacent of that edge, connect 4 vertices from that 4 cells.
 *
 * RF.
 * Original Paper. v1. [Tao Ju, Frank Losasso, Scott Schaefer, Joe Warren 2001]
 * https://www.cse.wustl.edu/~taoju/research/dualContour.pdf
 *
 * Official Paper. Dual. Contouring: "The Secret Sauce". [Scott Schaefer, Joe Warre 2001]
 * https://people.eecs.berkeley.edu/~jrs/meshpapers/SchaeferWarren2.pdf
 *
 * Basic Understanding and Related Advanced Information of D.C.
 * https://www.boristhebrave.com/2018/04/15/dual-contouring-tutorial/
 *
 * ISO Extraction P.A.
 * http://www.inf.ufrgs.br/~comba/papers/thesis/diss-leonardo.pdf
 *
 * QEF Solve Explanar .ALTERNATE STRATEGY
 * https://www.mattkeeter.com/projects/qef/#alternate-constraints
 *
 */
public final class DualContouringUniformGridDensitySmpl {


    public static final TrifFunc F_SPHERE = (x, y, z) ->
                   2.5f - (float)Math.sqrt(x*x + y*y + z*z);
    public static final TrifFunc F_CUBE = (x, y, z) -> { // x /=2;
        Vector3f dv = Vector3f.abs(new Vector3f(x,y,z));
        int maxAxis = Maths.maxi(dv.x, dv.y, dv.z);
        return 2.5f - Math.abs(dv.get(maxAxis));
    };
    public static final TrifFunc F_CYLINDER = (x, y, z) -> {
        if (Math.abs(y) < 3) return 2.5f - (float)Math.sqrt(x*x + z*z);
        if (y < 3) return 2.5f - (float)Math.sqrt(x*x + (y+3)*(y+3) + z*z);
        return 3-y;
    };
    static NoiseGeneratorPerlin noise = new NoiseGeneratorPerlin();
    public static final TrifFunc F_NOISE = (x, y, z) -> {
//        if (Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10)
//            return 0;
        return noise.noise(x/18, y/8f, z/18)-0.4f;
    };

    private static Vector3f computecellvertex(TrifFunc f, Vector3f p, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
//        if (true) return dest.set(p).addScaled(.5f, Vector3f.ONE);


        // evaluate f value at 8 corner-vert.
        float[][][] v = new float[2][2][2];
        for (int dx=0; dx<2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                for (int dz = 0; dz < 2; dz++) {
                    v[dx][dy][dz] = f.sample(p.x+dx, p.y+dy, p.z+dz);
                }
            }
        }

        // find sign-change edges. in cube cell 12 edges.
        // for each 4 edges in 3 axies.
        List<Vector3f> scedgeverts = new ArrayList<>();
        for (int dy=0; dy<2; dy++) {  // X AXIS EDGES.v
            for (int dz=0; dz<2; dz++) {
                if ((v[0][dy][dz]>0) != (v[1][dy][dz]>0))
                    scedgeverts.add(new Vector3f(Maths.inverseLerp(0, v[0][dy][dz], v[1][dy][dz]), dy, dz).add(p));
            }
        }
        for (int dx=0; dx<2; dx++) {
            for (int dz=0; dz<2; dz++) {
                if ((v[dx][0][dz]>0) != (v[dx][1][dz]>0))
                    scedgeverts.add(new Vector3f(dx, Maths.inverseLerp(0, v[dx][0][dz], v[dx][1][dz]), dz).add(p));
            }
        }
        for (int dx=0; dx<2; dx++) {
            for (int dy=0; dy<2; dy++) {
                if ((v[dx][dy][0]>0) != (v[dx][dy][1]>0))
                    scedgeverts.add(new Vector3f(dx, dy, Maths.inverseLerp(0, v[dx][dy][0], v[dx][dy][1])).add(p));
            }
        }

        if (scedgeverts.size() == 0)
            return null;
//        if (scedgeverts.size() == 1)
//            return scedgeverts.iterator().next();
//        if (scedgeverts.size() == 2)
//            return VertexUtil.centeravg(scedgeverts, dest);
//        if (true) return VertexUtil.centeravg(scedgeverts, dest);

        List<Vector3f> vertnorms = new ArrayList<>(scedgeverts.size());
        for (Vector3f vert : scedgeverts) {
            try {
                vertnorms.add(Maths.gradient(f, vert, null));
            } catch (ArithmeticException ex) {
                System.out.println("zero len f'(v)");
                vertnorms.add(new Vector3f(0, 1,0 ));
            }
        }

        assert scedgeverts.size() >= 3 && scedgeverts.size() <= 12 : "Unexcepted points size: "+scedgeverts.size();
        assert scedgeverts.size() == vertnorms.size();
        return solveqef(scedgeverts, vertnorms, dest);
    }

    /**
     * Minimize the Quadratic Error Function i.e. QEF.  [Scott Schaefer, Joe Warre 2001]
     * Solving use of Least Square.
     *
     *  E(x) = SUM (n_i·(x-v_i))^2
     *             (n_i·x-n_i·v_i)^2
     *             (Ax-b         )^2
     *             (Ax-b)^T (Ax-b)    // [Garland and Heckbert 1997] constant space usage. now only AtA, Atb, btb stored. whcih symmetric 3x3 matrix, 3x1 matrix and a scalar.
     *             xt(AtA)x - 2xt(Atb) + btb
     *
     *             x=(AtA)^-1 Atb     // Let the derivative of E be 0, x can be found.
     *  R. Linear.
     *              Ax-b
     *              Ax=b
     *              AtAx=Atb   // For constant 3x3, 3x1 matrices.
     *              x=(AtA)^-1 Atb
     */
    private static Vector3f solveqef(List<Vector3f> verts, List<Vector3f> norms, Vector3f dest) {

        dest.set(QEFSolvDCJAM3.wCalcQEF(verts, norms));
//        dest.set(QEFSolvNKG.doSlv(verts, norms));
//        dest.set(QEFSolvL20.doSLV(verts, norms));
//        dest.set(QEFSolvBFITR.getLeastSqBF(verts, norms));
//        dest.set(QEFSolvBFAVG.doAvg(verts, norms));
//        dest.set(QEFSolvAxbLINRLstSq.solvLstSq(verts, norms));

        return dest;
    }

    /* R.F.
     * https://adrianstoll.com/linear-algebra/least-squares.html  Least Squares Solve Ax=b.  MNCOMMENT Ax = b => AtAx = Atb => x ~ (AtA)'Atb.
     * https://www.cnblogs.com/monoSLAM/p/5252917.html            Least Square LINMAT Ax=b solve.
     * https://github.com/emilk/Dual-Contouring/blob/master/src/math/Solver.cpp                     QEF Solve. by emilk.
     * https://github.com/Lin20/isosurface/blob/master/Isosurface/Isosurface/QEFSolver/QEFSolver.cs [XNA cpp2cs] QEF Solve. Acc SVD.  alt https://github.com/tuckbone/DualContouringCSharp/blob/master/CSharpCode/DualContouring/QefSolver.cs  ORI: https://github.com/nickgildea/DualContouringSample/blob/master/DualContouringSample/qef.cpp
     * https://github.com/nickgildea/qef/blob/master/qef.cl                                         QEF Solve. SVD. pinv.
     * https://github.com/M0lion/DualContouring/blob/master/Assets/Scripts/QEF.cs                   QEF Solv. directly by libs
     * https://stackoverflow.com/questions/16734792/dual-contouring-and-quadratic-error-function    QEF Solve. people opinions.
     */


    private static class QuadFace {
        private Vector3f v1, v2, v3, v4;
        private QuadFace(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, boolean swap) {
            if (swap) { this.v1=v4; this.v2=v3; this.v3=v2; this.v4=v1; }
            else {      this.v1=v1; this.v2=v2; this.v3=v3; this.v4=v4; }
        }
        private Vector3f[] tri() {
            return new Vector3f[] { new Vector3f(v1), new Vector3f(v2), new Vector3f(v3), new Vector3f(v3), new Vector3f(v4), new Vector3f(v1)};
        }
    }

    public static Vector3f[] contouring(TrifFunc f, AABB aabb) {

        // for each 'valid' cell, find the interior vertex.
        Map<Vector3f, Vector3f> verts = new HashMap<>();
        for (float x=aabb.min.x; x<=aabb.max.x; x++) {
            for (float y=aabb.min.y; y<=aabb.max.y; y++) {
                for (float z=aabb.min.z; z<=aabb.max.z; z++) {
                    Vector3f p = new Vector3f(x, y, z);
                    Vector3f vert = computecellvertex(f, p, null);
                    if (vert != null)  // valid cell.
                        verts.put(p, vert);
                }
            }
        }

        // for each sign-change edge, connect its 4 adjacent cells vertex. CCW winding.
        List<QuadFace> faces = new ArrayList<>();
        boolean solid0, solid1;
        for (float x=aabb.min.x; x<=aabb.max.x; x++) {
            for (float y=aabb.min.y; y<=aabb.max.y; y++) {
                for (float z=aabb.min.z; z<=aabb.max.z; z++) {
                    solid0 = f.sample(x,y,z) > 0;
                    if (y > aabb.min.y && z > aabb.min.z) {  // X AXIS. edge.
                        solid1 = f.sample(x+1,y,z) > 0;
                        if (solid0 != solid1)  // is sign-change edge.
                            faces.add(new QuadFace(
                                    new Vector3f(x, y, z-1),
                                    new Vector3f(x, y, z),
                                    new Vector3f(x, y-1, z),
                                    new Vector3f(x, y-1, z-1), solid1));
                    }
                    if (x > aabb.min.x && z > aabb.min.z) {  // Y AXIS.
                        solid1 = f.sample(x, y+1, z) > 0;
                        if (solid0 != solid1)
                            faces.add(new QuadFace(
                                    new Vector3f(x, y, z-1),
                                    new Vector3f(x-1, y, z-1),
                                    new Vector3f(x-1, y, z),
                                    new Vector3f(x, y, z), solid1));
                    }
                    if (x > aabb.min.x && y > aabb.min.y) {  // Z-AXIS.
                        solid1 = f.sample(x, y, z+1) > 0;
                        if (solid0 != solid1)
                            faces.add(new QuadFace(
                                    new Vector3f(x, y, z),
                                    new Vector3f(x-1, y, z),
                                    new Vector3f(x-1, y-1, z),
                                    new Vector3f(x, y-1, z), solid1));
                    }
                }
            }
        }

        // get actually cell vertex.
        for (QuadFace q : faces) {
            q.v1.set(verts.get(q.v1));
            q.v2.set(verts.get(q.v2));
            q.v3.set(verts.get(q.v3));
            q.v4.set(verts.get(q.v4));
        }

        Vector3f[] trils = new Vector3f[faces.size() * 6];
        for (int i = 0;i < faces.size();i++) {
            System.arraycopy(faces.get(i).tri(), 0, trils, i*6, 6);
        }
        return trils;
    }

}
