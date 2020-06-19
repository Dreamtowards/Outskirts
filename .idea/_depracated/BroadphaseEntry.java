package outskirts.physics.collision.broadphase;

import outskirts.physics.collision.dispatch.CollisionObject;

/**
 * the Broadphase-PackageCovering of a CollisionObject
 * name as Entry..?
 */
public class BroadphaseEntry {

    private static int nextUniqueId = 0;

    private AABB aabb;

    public CollisionObject collisionObject;

    public final int uniqueId;

    public BroadphaseEntry(AABB aabb, CollisionObject collisionObject) {
        this.uniqueId = nextUniqueId++;
        this.aabb = aabb;
        this.collisionObject = collisionObject;
    }

    public AABB getAABB() {
        return aabb;
    }

// in OverlappingPairList.java
//    public static class OverlappingPair { //BroadphaseOverlappingPair
//
//        public final BroadphaseEntry e1;
//        public final BroadphaseEntry e2;
//        //CollisionAlgorithm, Object userInfo; = null
//        private long hash;
//
//        public CollisionAlgorithm collisionAlgorithm;
//
//        //needs static of() for specially constructor operation..?
//        public OverlappingPair(BroadphaseEntry w1, BroadphaseEntry w2) {
//            Validate.isTrue(w1.uniqueId != w2.uniqueId, "w can't be same");
//            if (w1.uniqueId < w2.uniqueId) {
//                this.e1 = w1;
//                this.e2 = w2;
//            } else {
//                this.e1 = w2;
//                this.e2 = w1;
//            }
//            this.hash = OverlappingPair.hashCode(this.e1, this.e2);
//        }
//
//        public static long hashCode(BroadphaseEntry w1, BroadphaseEntry w2) {
//            Validate.isTrue(w1.uniqueId != w2.uniqueId, "w can't be same");
//            if (w1.uniqueId > w2.uniqueId) {
//                BroadphaseEntry tmp = w1;
//                w1 = w2;
//                w2 = tmp;
//            }
//            return ((w1.uniqueId & 0xFFFFFFFFL) << 32) | (w2.uniqueId & 0xFFFFFFFFL);
//        }
//
//        @Override
//        public int hashCode() {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            return  obj instanceof OverlappingPair &&
//                    ((OverlappingPair)obj).e1 == this.e1 &&
//                    ((OverlappingPair)obj).e2 == this.e2;
//        }
//    }
}
