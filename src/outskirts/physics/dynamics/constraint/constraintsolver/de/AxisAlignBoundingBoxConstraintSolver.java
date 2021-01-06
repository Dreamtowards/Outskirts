package outskirts.physics.dynamics.constraint.constraintsolver.de;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.dynamics.RigidBody;
import outskirts.physics.dynamics.constraint.Constraint;
import outskirts.physics.dynamics.constraint.constraintsolver.ConstraintSolver;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.List;

public class AxisAlignBoundingBoxConstraintSolver extends ConstraintSolver {

    @Override
    public void solveGroup(List<CollisionObject> collisionObjects, List<CollisionManifold> collisionManifolds, List<Constraint> constraints, float delta) {

        for (CollisionManifold mnf : collisionManifolds) {
            RigidBody bodyA = mnf.bodyA();
            RigidBody bodyB = mnf.bodyB();

            Vector3f dPos = new Vector3f(bodyB.getLinearVelocity()).scale(delta);

            AABB bodyAABB = bodyB.getAABB();
            AABB otherAABB = bodyA.getAABB();

                float x = aabbAxisSepf(bodyAABB, otherAABB, 0);
                float y = aabbAxisSepf(bodyAABB, otherAABB, 1);
                float z = aabbAxisSepf(bodyAABB, otherAABB, 2);

            int mni = Math.abs(x)<Math.abs(y)? Math.abs(x)<Math.abs(z)?0:2 : Math.abs(y)<Math.abs(z)?1:2;
            Vector3f.set(bodyB.getLinearVelocity(), mni, 0);
            Vector3f.set(dPos, mni, mni==0?x:mni==1?y:z);
//            Log.LOGGER.info("x:{}, y:{}, z:{}",x,y,z);

            if (mni==1)
                bodyB.getLinearVelocity().scale(0.91f); //tmp friction

            bodyB.transform().origin.add(dPos);
        }


    }

    private static float aabbAxisSepf(AABB bodyAabb, AABB statiAabb, int axis) {
        float b2t = statiAabb.min.get(axis) - bodyAabb.max.get(axis); // moDir>0
        float t2b = statiAabb.max.get(axis) - bodyAabb.min.get(axis);
        return Math.abs(b2t) < Math.abs(t2b) ? b2t : t2b;
    }
}
