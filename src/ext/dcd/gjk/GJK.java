package ext.dcd.gjk;

import outskirts.util.vector.Vector3f;

public class GJK {

    public static Vector3f support(Shape shape1, Shape shape2, Vector3f d) {
        Vector3f p1 = shape1.getFarthestPoint(d);
        Vector3f p2 = shape2.getFarthestPoint(new Vector3f(d).negate());
        return Vector3f.sub(p1, p2, null);
    }

    public static boolean collisionDetect(Shape s1, Shape s2) {
        Vector3f[] simplex = new Vector3f[3]; // simplex just maximum stores 3 points

        Vector3f d = Vector3f.sub(s1.position, s2.position, null);

        _arraypush(simplex, support(s1, s2, d));

        d.negate();

        while (true) {
            Vector3f lst = support(s1, s2, d);
            _arraypush(simplex, lst);

            if (Vector3f.dot(lst, d) <= 0) {
                return false;
            } else {
                if (containsOrigin_AndUpdateDirection(simplex, d)) {
                    return true;
                }
            }
        }
    }

    public static boolean containsOrigin_AndUpdateDirection(Vector3f[] simplex, Vector3f d) {
        Vector3f p1 = simplex[0];
        Vector3f p2 = simplex[1];

        Vector3f line_p1p2 = Vector3f.sub(p2, p1, null);// v1 to v2
        Vector3f line_p1o = Vector3f.sub(Vector3f.ZERO, p1, null); // v1 to origin

        if (simplex.length == 3) {
            Vector3f p3 = simplex[2];
            Vector3f line_p1p3 = Vector3f.sub(p3, p1, null); // v1 to v3

            Vector3f norm_v1v2 = triCross(line_p1p3, line_p1p2, line_p1p2, null);
            Vector3f norm_v1v3 = triCross(line_p1p2, line_p1p3, line_p1p3, null);

            if (Vector3f.dot(norm_v1v2, line_p1o) > 0f) {

                _arraypop(simplex, p3);

                d.set(norm_v1v2);

                return false;
            } else if (Vector3f.dot(norm_v1v3, line_p1o) > 0f) {

                _arraypop(simplex, p2);

                d.set(norm_v1v3);

                return false;
            } else { //R2 is no possible...?
                return true;
            }
        } else if (simplex.length == 2) {

            triCross(line_p1p2, line_p1o, line_p1p2, d);

            return false;
        } else {
            throw new RuntimeException("Illegal simplex vertex count.");
        }
    }

    private static Vector3f triCross(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        Vector3f.cross(v1, v2, dest);
        Vector3f.cross(dest, v3, dest);
        return dest;
    }





    private static boolean _arraypop(Object[] array, Object element) {
        int maxIndex = array.length-1;
        for (int i = 0;i < array.length;i++) {
            if (array[i] == element) {
                System.arraycopy(array, Math.min(i + 1, maxIndex), array, i, array.length - i - 1);
                array[maxIndex] = null;
                return true;
            }
        }
        return false;
    }

    private static void _arraypush(Object[] array, Object element) {
        int pushIndex = -1;
        for (int i = 0;i < array.length;i++) {
            if (array[i] == null) {
                pushIndex = i;
                for (int j = i+1;j < array.length;j++) {
                    if (array[j] != null) {
                        throw new IllegalStateException("cannot insert push.");
                    }
                }
                break;
            }
        }
        array[pushIndex] = element;
    }








    // distance AND closest points

    public static Vector3f closestPoint(Vector3f lA, Vector3f lB, Vector3f point) {

        Vector3f lineAB = Vector3f.sub(lB, lA, null); // line A to B
        Vector3f lineAP = Vector3f.sub(point, lA, null); // line A to P

        float ab_dot_ab = Vector3f.dot(lineAB, lineAB); // lengthSquared
        float ab_dot_ap = Vector3f.dot(lineAB, lineAP);

        float d = ab_dot_ap / ab_dot_ab;

        return lineAB.scale(d).add(lA);
    }


}
