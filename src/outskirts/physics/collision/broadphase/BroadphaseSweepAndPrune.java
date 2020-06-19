package outskirts.physics.collision.broadphase;

import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.dispatch.CollisionObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BroadphaseSweepAndPrune extends Broadphase {

    private static final Comparator<? extends CollisionObject> COMP_AABB_X_MIN_ASC =
            (body1, body2) -> (int)Math.signum(body1.getAABB().min.x - body2.getAABB().min.x);

    @Override
    public void addObject(CollisionObject body) {

    }

    @Override
    public void removeObject(CollisionObject body) {

    }

    @Override
    public void calculateOverlappingPairs() {
//        List<BroadphasePair> overlappingPairs = new ArrayList<>();

//        CollectionUtils.quickSort((List)broadphaseObjects, COMP_AABB_X_MIN_ASC); // this will shuffle the world objects
//
//        for (int i = 0;i < broadphaseObjects.size();i++) {
//            CollisionObject bodyOuter = broadphaseObjects.get(i);
//
//            for (int j = i+1;j < broadphaseObjects.size();j++) {
//                CollisionObject bodyInner = broadphaseObjects.get(j);
//
//                if (bodyInner.getAABB().min.x < bodyOuter.getAABB().max.x) {
//                    overlappingPairs.add(BroadphasePair.of(bodyInner, bodyOuter, null));
//                } else {
//                    break;
//                }
//            }
//        }
    }
}
