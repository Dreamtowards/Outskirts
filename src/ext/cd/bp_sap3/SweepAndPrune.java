package ext.cd.bp_sap3;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class SweepAndPrune {

    private static final Comparator<AABB> AABB_X_MIN_ASC = (a, b) -> (int)Math.signum(a.min.x - b.min.x);

    public static void sap(List<AABB> aabbList) {

        List<AABB> collidedList = new ArrayList<>(); //pair

        CollectionUtils.quickSort(aabbList, AABB_X_MIN_ASC);

        for (int i = 0;i < aabbList.size();i++) {
            AABB aabbOuter = aabbList.get(i);

            for (int j = i+1; j < aabbList.size();j++) {
                AABB aabbInner = aabbList.get(j);

                if (aabbInner.min.x < aabbOuter.max.x) {
                    if (AABB.intersects(aabbInner, aabbOuter)) {
                        collidedList.add(aabbInner);
                        collidedList.add(aabbOuter);
                    }
                } else {
                    break;
                }
            }
        }

    }

}
