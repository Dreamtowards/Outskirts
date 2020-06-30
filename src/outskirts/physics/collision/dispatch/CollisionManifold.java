package outskirts.physics.collision.dispatch;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.narrowphase.Narrowphase;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.Transform;
import outskirts.util.Validate;
import outskirts.util.vector.Vector3f;

import java.util.List;

/**
 * PersistentManifold is a Contact-Point cache, it stays persistent as long as objects
 * are overlapping in broadphase. Those Contact-Points are created by the narrowphase.
 *
 * The cache can be empty, or hold 1, 2, 3 or 4 points. update/refresh old Contact-Points,
 * and throw them away if necessary (distance (with excepted) becomes too large).
 *
 * Reduces the cache to 4 points, when more than 4 points are added, using following rules:
 * the Contact-Point with deepest penetration is always kept, and it tries to maximize the area covered by the points.
 *
 * Note that some pairs of objects might have more than one Contact-Manifold.  (..???
 *
 * :jezek2
 */
public final class CollisionManifold {

    private static final float MIN_CONTACT_POINT_DISTANCE = 0.02F;
    public static final int MAX_CONTACT_POINTS = 4;

    // contact Points cache
    private ContactPoint[] contactPoints = CollectionUtils.fill(new ContactPoint[MAX_CONTACT_POINTS], ContactPoint::new);
    private int numContactPoints = 0;

    public int cpAddedCount = 0;  // inserted.?

    // refer to Execution-CDNarrowphaseDispatcher.
    public Narrowphase narrowphase;

    private final RigidBody bodyA; // shouldn't public .? use method, given NotLos, people dont trus property-field
    private final RigidBody bodyB;

    public CollisionManifold(RigidBody bodyA, RigidBody bodyB) { // most contactpoints always as in excepted
        // makes the bodyB as either body of A,B which "smaller" volume.
        // because ContactPoint-OnB is sample on bodyB, for more "higher" ContactPoint sample precision,
        // just makes the bodyB's volume as small as possible.
        // todo: this switch may should move to Gjk ConvexConvex execution tmp-swap.
        if (AABB.volume(bodyB.getAABB()) < AABB.volume(bodyA.getAABB())) {
            this.bodyA = bodyA;
            this.bodyB = bodyB;
        } else {this.bodyA = bodyB;this.bodyB = bodyA;}
    }

    public RigidBody bodyA() {
        return bodyA;
    }
    public RigidBody bodyB() {
        return bodyB;
    }

    public final boolean containsBody(CollisionObject body) {
        return bodyA==body || bodyB==body;
    }

    public int getNumContactPoints() {
        return numContactPoints;
    }

    // index < numContactPoints
    public ContactPoint getContactPoint(int i) {
        return contactPoints[i];
    }

    /**
     * @param normOnB Collision-Surface-Normal onB, worldspace, unit-vector
     * @param penetration must be > 0
     * @param pointOnB Contact-Point onB, worldspace
     */
    public final void addContactPoint(Vector3f normOnB, float penetration, Vector3f pointOnB) {
        Validate.isTrue(penetration > 0, "Illegal penetration, penetration should be > 0. actual: %s", penetration); // needs opt ..?

        // check the distance with other points. fliter.
        Vector3f TMP = new Vector3f();
        for (ContactPoint cp : contactPoints) {
            if (Vector3f.sub(cp.pointOnB, pointOnB, TMP).lengthSquared() < MIN_CONTACT_POINT_DISTANCE*MIN_CONTACT_POINT_DISTANCE)
                return;
        }
        // insert. went capacity
        int insertIndex = numContactPoints;
        if (numContactPoints == MAX_CONTACT_POINTS) {
            if (MAX_CONTACT_POINTS >= 4) {
                insertIndex = sortCachedContactPoints(penetration, Transform.inverseTranform(bodyB.transform(), new Vector3f(pointOnB)));
            } else {
                insertIndex = 0;
            }
        } else {
            numContactPoints++;
        }

//        numContactPoints = 1;insertIndex=0; // tmp test
        ContactPoint cp = contactPoints[insertIndex];
        cp.reset();

        // setup localpointA/B
        Vector3f pointOnA = new Vector3f(pointOnB).addScaled(-penetration, normOnB);
        Transform.inverseTranform(bodyA.transform(), cp.localpointA.set(pointOnA));
        Transform.inverseTranform(bodyB.transform(), cp.localpointB.set(pointOnB));

        cp.normOnB.set(normOnB);

        cp.combined_friction = Maths.sqrt(bodyA.getFriction() * bodyB.getFriction());
        cp.combined_restitution = Maths.sqrt(bodyA.getRestitution() * bodyB.getRestitution());

        // other Contact-Point's information just wait been update/setup by refreshContactPoints() in later (when finished "this" Collision-Detection).

        cpAddedCount++;
    }

    /**
     * @return max area cp index.
     */
    private int sortCachedContactPoints(float cp_penetration, Vector3f cp_localpointB) {

        int   maxpen_i = -1;
        // keep max penetration Contact-Point
        float maxpen = cp_penetration;
        for (int i = 0;i < MAX_CONTACT_POINTS;i++) {
            if (contactPoints[i].penetration > maxpen) {
                maxpen_i = i;
                maxpen = contactPoints[i].penetration;
            }
        }

        float[] res = new float[4];

        if (maxpen_i != 0) {
            Vector3f a0 = Vector3f.sub(              cp_localpointB, contactPoints[1].localpointB, null); // v1 -> cp
            Vector3f b0 = Vector3f.sub(contactPoints[3].localpointB, contactPoints[2].localpointB, null); // v2 -> v3
            res[0] = Vector3f.cross(a0, b0, null).lengthSquared();
        }

        if (maxpen_i != 1) {
            Vector3f a1 = Vector3f.sub(              cp_localpointB, contactPoints[0].localpointB, null); // v0 -> cp
            Vector3f b1 = Vector3f.sub(contactPoints[3].localpointB, contactPoints[2].localpointB, null); // v2 -> v3
            res[1] = Vector3f.cross(a1, b1, null).lengthSquared();
        }

        if (maxpen_i != 2) {
            Vector3f a2 = Vector3f.sub(              cp_localpointB, contactPoints[0].localpointB, null); // v0 -> cp
            Vector3f b2 = Vector3f.sub(contactPoints[3].localpointB, contactPoints[1].localpointB, null); // v1 -> v3
            res[2] = Vector3f.cross(a2, b2, null).lengthSquared();
        }

        if (maxpen_i != 3) {
            Vector3f a3 = Vector3f.sub(              cp_localpointB, contactPoints[0].localpointB, null); // v0 -> cp
            Vector3f b3 = Vector3f.sub(contactPoints[2].localpointB, contactPoints[1].localpointB, null); // v1 -> v2
            res[3] = Vector3f.cross(a3, b3, null).lengthSquared();
        }

        int maxarea_i = 0;
        for (int i = 0;i < 4;i++) {
            if (res[i] > res[maxarea_i])
                maxarea_i = i;
        }
        return maxarea_i;
    }

    public final void refreshContactPoints() {
        // update information
        for (int i = 0;i < numContactPoints;i++) {
            ContactPoint cp = contactPoints[i];

            // update worldspace pointOnA/B
            Transform.transform(bodyA.transform(), cp.pointOnA.set(cp.localpointA));
            Transform.transform(bodyB.transform(), cp.pointOnB.set(cp.localpointB));

            // update worldspace relpointOnA/B rA/rB
            cp.rA.set(cp.pointOnA).sub(bodyA.transform().origin);
            cp.rB.set(cp.pointOnB).sub(bodyB.transform().origin);

            // update penetration
            Vector3f pBA = Vector3f.sub(cp.pointOnA, cp.pointOnB, null);
            cp.penetration = -Vector3f.dot(pBA, cp.normOnB);
        }

        // check vaild. filter
        for (int i = numContactPoints-1;i >= 0;i--) {
            ContactPoint cp = contactPoints[i];
            if (cp.penetration < 0) {
                removeContactPoint(i);
            } else {
                Vector3f stdB = new Vector3f(cp.pointOnA).addScaled(cp.penetration, cp.normOnB); // "expected" pointOnB
                if (Vector3f.sub(cp.pointOnB, stdB, null).lengthSquared() > 0.2f) {
                    removeContactPoint(i);
                }
            }
        }
    }

    private void removeContactPoint(int i) {
        CollectionUtils.swap(contactPoints, i, --numContactPoints);
    }

    public static final class ContactPoint {
        private ContactPoint() {}

        public final Vector3f pointOnB = new Vector3f(); // [-tmp] worldspace Contact-Point. been update by localpointB.
        public final Vector3f pointOnA = new Vector3f(); // [tmp][convenience] this is unnecessary. its == pointOnB + (-penetration * normOnB). there just for using-convinian-cache

        public final Vector3f rA = new Vector3f(); // [tmp][convenience] worldspace relpointOnA, i.e pointOnA - bodyA.transform().origin.
        public final Vector3f rB = new Vector3f(); // [tmp][convenience]                             pointOnB - bodyB.transform().origin.

        public final Vector3f localpointB = new Vector3f(); // localspace
        public final Vector3f localpointA = new Vector3f();

        public final Vector3f normOnB = new Vector3f(); // worldspace Collision-Normal. todo: why not localspace norm..?

        public float penetration; // Contact-Penetration-Depth. must be > 0, (the value conceptly equals -surfaceDistan

        public float combined_friction;
        public float combined_restitution;

        public ConstraintSolverPresistentData cpd = new ConstraintSolverPresistentData();

        private void reset() {
               pointOnB.set(0,0,0);   pointOnA.set(0,0,0);
                     rA.set(0,0,0);         rB.set(0,0,0);
            localpointB.set(0,0,0);localpointA.set(0,0,0);
                normOnB.set(0,0,0);
            penetration=0;
            combined_friction=0;
            combined_restitution=0;
            cpd.reset();
        }

        public static class ConstraintSolverPresistentData {

            public float normalImpulseSum;
            public float tangentImpulseSum1;
            public float tangentImpulseSum2;

            public float normalEffectiveMass;  // λ denominator, contact constraint.
            public float tangentEffectiveMass1; // λ denominator, friction constraint. both for A and B.
            public float tangentEffectiveMass2;
            public Vector3f tangent1 = new Vector3f(); // frictionTangent1. worldspace. both for A and B
            public Vector3f tangent2 = new Vector3f();

            public float rest_combined_restitution;

            private void reset() {
                normalImpulseSum=0;
                tangentImpulseSum1=0;
                tangentImpulseSum2=0;

                normalEffectiveMass=0;
                tangentEffectiveMass1=0;
                tangentEffectiveMass2=0;
                rest_combined_restitution=0;
                tangent1.set(0,0,0);
                tangent2.set(0,0,0);
            }
        }
    }


    public static int indexOf(List<CollisionManifold> l, CollisionObject b1, CollisionObject b2) {
        for (int i = 0;i < l.size();i++) {
            CollisionManifold m = l.get(i);
            if (m.containsBody(b1) && m.containsBody(b2))
                return i;
        }
        return -1;
    }
}
