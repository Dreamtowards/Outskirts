package outskirts.physics.dynamics.constraint.constraintsolver.de;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.dynamics.RigidBody;
import outskirts.physics.dynamics.constraint.Constraint;
import outskirts.physics.dynamics.constraint.constraintsolver.ConstraintSolver;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.List;

public class AxisAlignBoundingBoxConstraintSolver extends ConstraintSolver {

    private World world;

    public AxisAlignBoundingBoxConstraintSolver(World world) {
        this.world = world;
    }

    @Override
    public void solveGroup(List<CollisionObject> collisionObjects, List<CollisionManifold> collisionManifolds, List<Constraint> constraints, float delta) {


        for (CollisionObject co : collisionObjects) {
            RigidBody body = (RigidBody)co;

            Vector3f dPos = new Vector3f(body.getLinearVelocity()).scale(delta);
            Vector3f dPosOri = new Vector3f(body.getLinearVelocity()).scale(delta);

            AABB bodyAABB = body.getAABB();

            for (AABB aabb : world.getAABBs(new AABB(bodyAABB).expand(dPos))) {
                if (aabb == bodyAABB)
                    continue;

                dPos.x = bodyAABB.calculateXConstraint(aabb, dPos.x);

                dPos.z = bodyAABB.calculateZConstraint(aabb, dPos.z);

                dPos.y = bodyAABB.calculateYConstraint(aabb, dPos.y);
            }

            if (dPosOri.x != dPos.x)
                body.getLinearVelocity().x = 0;
            if (dPosOri.y != dPos.y) {
                body.getLinearVelocity().scale(0.91f); //tmp friction
                body.getLinearVelocity().y = 0;
            }
            if (dPosOri.z != dPos.z)
                body.getLinearVelocity().z = 0;

//            bodyAABB.translate(dPos);
            body.getLinearVelocity().addScaled(1f/delta, dPos);
        }


    }
}
