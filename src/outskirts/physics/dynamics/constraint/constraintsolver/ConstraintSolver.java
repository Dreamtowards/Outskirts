package outskirts.physics.dynamics.constraint.constraintsolver;

import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.dynamics.constraint.Constraint;

import java.util.List;

public abstract class ConstraintSolver {

    public abstract void solveGroup(List<CollisionObject> bodies, List<CollisionManifold> manifolds, List<Constraint> constraints, float delta);

}
