package outskirts.physics.dynamics;

import outskirts.client.Outskirts;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionWorld;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.dynamics.constraint.constraintsolver.ConstraintSolver;
import outskirts.physics.dynamics.constraint.constraintsolver.SequentialImpulseConstraintSolver;
import outskirts.util.Transform;
import outskirts.util.vector.Vector3f;

import java.util.Collections;
import java.util.List;

public class DiscreteDynamicsWorld extends CollisionWorld {

//         islandManager, vehicles, sleepstatus;

    protected ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();

    private List<CollisionManifold> collisionManifolds;

    public DiscreteDynamicsWorld() { }

    public void stepSimulation(float delta) {

        // *** Predict Unconstrained Motion ***

        Outskirts.getProfiler().push("predictMotion");
        predictUnconstrainedMotion(delta); // integrate velocities by current forces, then damping..
        Outskirts.getProfiler().pop("predictMotion");

        // *** Discrete Collision Detection ***

        Outskirts.getProfiler().push("DCD");
        performDiscreteCollisionDetection();
        Outskirts.getProfiler().pop("DCD");

        // *** Constraint Solve ***

        // calculateSimulationIslands(); // :opt

        Outskirts.getProfiler().push("Solve");
        solveConstraints(delta);
        Outskirts.getProfiler().pop("Solve");

        // ** Update Transforms **

        Outskirts.getProfiler().push("IntegrateTrans");
        integrateTransforms(delta);
        Outskirts.getProfiler().pop("IntegrateTrans");

        // updateActivationState(timeStep); // :opt // when sleeping enabled

    }

    private void predictUnconstrainedMotion(float delta) {
        Vector3f TMP = new Vector3f();
        for (CollisionObject co : collisionObjects) { RigidBody body = (RigidBody)co;

            body.applyForce(TMP.set(body.getGravity()).scale(body.getMass())); // [sep][minor] F=am. applyGravity().

            body.refreshInertiaTensorWorld(); // update invInertiaTensorWorld. used in subsequent calculations.

            {
                body.integrateVelocities(delta);

                body.performDamping(delta);
            }

            body.clearForces(); // [sep] clearForces().
        }
    }

    private void performDiscreteCollisionDetection()
    {
        Outskirts.getProfiler().push("updateAABBs");
        updateAABBs();
        Outskirts.getProfiler().pop("updateAABBs");

        Outskirts.getProfiler().push("Broadphase");
        broadphase.calculateOverlappingPairs();
        Outskirts.getProfiler().pop("Broadphase");

        Outskirts.getProfiler().push("Narrowphase");
        collisionManifolds = narrowphase.detectCollisions(broadphase.getOverlappingPairs());
        Outskirts.getProfiler().pop("Narrowphase");
    }

    private void solveConstraints(float delta)
    {
        constraintSolver.solveGroup(collisionObjects, getCollisionManifolds(), null, delta);
    }

    private void integrateTransforms(float delta) {
        for (CollisionObject co : collisionObjects) { RigidBody body = (RigidBody)co;
            Transform.integrate(body.transform(), body.getLinearVelocity(), body.getAngularVelocity(), delta);
        }
    }







    @Override
    public void addCollisionObject(CollisionObject collisionObject) {
        broadphase.addObject(collisionObject);

        super.addCollisionObject(collisionObject);
    }

    @Override
    public void removeCollisionObject(CollisionObject collisionObject) {
        broadphase.removeObject(collisionObject);

        super.removeCollisionObject(collisionObject);
    }

    public List<CollisionManifold> getCollisionManifolds() {
        return Collections.unmodifiableList(collisionManifolds);
    }
}
