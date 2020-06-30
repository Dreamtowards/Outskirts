package outskirts.physics.extras.quickhull;

import outskirts.client.gui.debug.GuiVert3D;
import outskirts.util.*;
import outskirts.util.vector.Vector3f;

import java.util.*;

/**
 * ConvexHull
 * O(n log n) timecomplixity
 */
public class QuickHull {

    private static final float EPSILON = 0.0001f;


    private static Vector3f[] buildInitTetrahedronVset(Set<Vector3f> vertices) {
        Vector3f A=null, B=null, C=null, D=null;

        Vector3f[] mxmnxyz = new Vector3f[6]; // index of max,min of XYZ.    refer from Set<Vector3f> input. so should be Readonly.
        for (Vector3f v : vertices) {
            if (mxmnxyz[0]==null) Arrays.fill(mxmnxyz, v); // init.
            if (v.x > mxmnxyz[0].x) mxmnxyz[0]=v;
            if (v.y > mxmnxyz[1].y) mxmnxyz[1]=v;
            if (v.z > mxmnxyz[2].z) mxmnxyz[2]=v;
            if (v.x < mxmnxyz[3].x) mxmnxyz[3]=v;
            if (v.y < mxmnxyz[4].y) mxmnxyz[4]=v;
            if (v.z < mxmnxyz[5].z) mxmnxyz[5]=v;
        }
        // find points A,B. which two points makeup the longest LineSegment in the 6 points.
        float mxLenSq = 0;
        for (Vector3f v1 : vertices) {
            for (Vector3f v2 : vertices) {
                if (v1==v2) continue;
                float lenSq = Vector3f.sub(v1, v2, null).lengthSquared();
                if (lenSq > mxLenSq) {
                    mxLenSq=lenSq;
                    A=v1; B=v2;
                }
            }
        }
        // find point C. which is a point farthest from the AB-LineSegment. in the 6 points.
        mxLenSq = 0;
        for (Vector3f v : vertices) {
            if (v==A || v==B) continue;
            float lenSq = Vector3f.sub(v, Maths.findClosestPointOnLineSegment(v, A, B, null), null).lengthSquared();
            if (lenSq > mxLenSq) {
                mxLenSq = lenSq;
                C=v;
            }
        }
        // find point D. which is a point farthest from the ABC-Triangle along the Normal. whether either norm side. in all points.
        float mxDstan = 0;
        Vector3f ABCNorm = Vector3f.trinorm(A, B, C, null, null);  // extension norm.
        for (Vector3f v : vertices) {
            float dstan = Math.abs(Vector3f.dot(ABCNorm, Vector3f.sub(v, A, null)));
            if (dstan > mxDstan) {
                mxDstan = dstan;
                D=v;
            }
        }

        // Normal.S
        Vector3f ABCDCenter = new Vector3f().add(A).add(B).add(C).add(D).scale(1/4f);

        Vector3f CA = Vector3f.sub(A, ABCDCenter, null); // Center->A
        if (Vector3f.dot(ABCNorm, CA) < 0)
            ABCNorm.negate();
        ABCNorm.normalize();

        Vector3f CD = Vector3f.sub(D, ABCDCenter, null); // Center->D
        Vector3f ABDNorm = Vector3f.trinorm(A,B,D,CD,null).normalize();
        Vector3f BCDNorm = Vector3f.trinorm(B,C,D,CD,null).normalize();
        Vector3f CADNorm = Vector3f.trinorm(C,A,D,CD,null).normalize();

        return new Vector3f[]{A,B,C,D,ABCNorm,ABDNorm,BCDNorm,CADNorm};
    }


    // tool method. input "can" duplicated vertices.
    public static Set<Vector3f> quickHull(float[] dupvts) {
        Set<Vector3f> s = new HashSet<>();
        Vector3f TMP = new Vector3f();
        for (int i = 0;i < dupvts.length;i+=3) {
            s.add(new Vector3f(dupvts[i], dupvts[i+1], dupvts[i+2]));
//            if (s.add(TMP.set(dupvts[i], dupvts[i+1], dupvts[i+2]))) { // prevents add-fault but new useless. - only new when add success.
//                TMP = new Vector3f();
//            }
        }
        return quickHull(s);
    }
    /**
     * input no duplicated points. out put no duplicated.
     * @return hull vertices. (reference is the param Set<Vector3f> vertices.)
     */
    public static Set<Vector3f> quickHull(Set<Vector3f> vertices) {

        Vector3f[] slx = buildInitTetrahedronVset(vertices);
        Vector3f A=slx[0], B=slx[1], C=slx[2], D=slx[3];
        Vector3f ABCNorm=slx[4],ABDNorm=slx[5],BCDNorm=slx[6],CADNorm=slx[7];

        List<Triangle> triangles = new ArrayList<>();
        List<Vector3f> verts = new ArrayList<>(vertices);

        triangles.add(new Triangle(A,B,C,ABCNorm, verts)); //verts.removeAll(triangles.get(0).pointsInfront);
        triangles.add(new Triangle(A,B,D,ABDNorm, verts));
        triangles.add(new Triangle(B,C,D,BCDNorm, verts));
        triangles.add(new Triangle(C,A,D,CADNorm, verts));

        for (int i = 0;i < triangles.size();) { Triangle t = triangles.get(i);
            if (t.P != null) {  // had front-points
                quickHull(t, triangles);
                i=0;
            } else {
                i++;
            }
        }

        vertices.clear();
        for (Triangle t : triangles) {
            vertices.add(t.v0); vertices.add(t.v1); vertices.add(t.v2);
        }
        return vertices;
    }

    private static void quickHull(Triangle trig, List<Triangle> triangles) {
        List<Vector3f> ls = new ArrayList<>();

        isCanContinue();

        List<Vector3f[]> bEdges = new ArrayList<>();
        for (int i = triangles.size()-1;i >= 0;i--) { Triangle t = triangles.get(i);
            // the triangle can been 'seem' from P
            if (Vector3f.dot(t.normal, Vector3f.sub(trig.P, t.v0, null)) > 0) {
                addBoundaryEdge(bEdges, t.v0, t.v1);
                addBoundaryEdge(bEdges, t.v1, t.v2);
                addBoundaryEdge(bEdges, t.v2, t.v0);
                triangles.remove(i);
                ls.addAll(t.pointsInfront);

//                GuiVert3D.INSTANCE.vertices.removeAll(t.vs);
                isCanContinue();
            }
        }

        Vector3f ABCPCenter = new Vector3f().add(trig.v0).add(trig.v1).add(trig.v2).add(trig.P).scale(1/4f);
        Vector3f CP = Vector3f.sub(trig.P, ABCPCenter, null);  // Center->P

        for (Vector3f[] e : bEdges) {
            triangles.add(new Triangle(e[0],e[1],trig.P, Vector3f.trinorm(e[0],e[1],trig.P,CP,null).normalize(), ls));

            isCanContinue();
        }
    }

    private static void addBoundaryEdge(List<Vector3f[]> bEdges, Vector3f A, Vector3f B) {
        if (!bEdges.removeIf(e -> (e[0]==A && e[1]==B) || (e[0]==B && e[1]==A))) {
            bEdges.add(new Vector3f[]{A, B});
        }
    }
    private static class Triangle {
        private Vector3f v0, v1, v2;
        private Vector3f normal;

        private List<Vector3f> pointsInfront = new ArrayList<>();
        private Vector3f P; // Nullable. the point farthest along the normal. when null, just no point infront of the triangle anymore.

        List<GuiVert3D.Vert> vs;

        public Triangle(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f normal, List<Vector3f> vertices) {
            this.v0 = v0;this.v1 = v1;this.v2 = v2;this.normal = normal;

            // find vertex P. which is max distance along the Norm
            float mxDistan = EPSILON;  // the Epsilon for prevents getting P from A/B/C
            Vector3f TMP = new Vector3f();
            for (Vector3f v : vertices) {
                float distan = Vector3f.dot(normal, Vector3f.sub(v, v0, TMP));
                if (distan > mxDistan) {
                    mxDistan = distan;
                    P = v;
                }
                if (distan > EPSILON) {
                    pointsInfront.add(v);
                }
            }
            // vertices.removeAll(pointsInfront); // for no duplicated search in futrue. but when no, ok yet.

//            vs = GuiVert3D.addTri("hull", v0, v1, v2, Colors.YELLOW, normal);
//            if (P!=null) {
//                GuiVert3D.Vert v = GuiVert3D.addVert("hull.P", P, Colors.GOLD);
//            }
        }
    }


    private static void isCanContinue() {
//        while (true) {
//            SystemUtils.sleep(10);
//            if (Outskirts.isKeyDown(GLFW.GLFW_KEY_K))
//                break;
//        }
    }

}
