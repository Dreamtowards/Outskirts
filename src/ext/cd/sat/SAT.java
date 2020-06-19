package ext.cd.sat;

import outskirts.util.vector.Vector3f;

public class SAT {

    public static MTV collisionDetect(Shape shape1, Shape shape2) {
        MTV mtv = new MTV();
        mtv.overlap = Float.MAX_VALUE;

        Vector3f[] axes = contactArray(
                shape1.getSeparatingAxes(),
                shape2.getSeparatingAxes()
        );

//        if (shape1.isCircle() && shape2.isCircle()) {
//            axes = new Vector3f[] {
//                    Vector3f.sub(shape1.position, shape2.position, null).normalize()
//            };
//        }

        for (Vector3f axis : axes) {
            Projection p1 = shape1.project(axis);
            Projection p2 = shape2.project(axis);

            if (!p1.isOverlap(p2)) {
                return null;
            } else {
                float o = p1.getOverlap(p2);
                // ... check containment
                if (o < mtv.overlap) {
                    mtv.overlap = o;
                    mtv.axis = axis;
                }
            }
        }

        return mtv;
    }


    public static <T> T[] contactArray(T[]... arrays) {
        int length = 0;
        for (T[] arr : arrays)
            length += arr.length;
        T[] r = (T[])new Object[length];
        int i = 0;
        for (T[] arr : arrays) {
            System.arraycopy(arr, 0, r, i, arr.length);
            i += arr.length;
        }
        return r;
    }

}
