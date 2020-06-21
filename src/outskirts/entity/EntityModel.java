package outskirts.entity;

import outskirts.physics.collision.shapes.concave.TriangleMeshShape;
import outskirts.physics.collision.shapes.convex.ConvexHullShape;
import outskirts.physics.dynamics.RigidBody;
import outskirts.physics.extras.quickhull.QuickHull;
import outskirts.util.vector.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class EntityModel extends Entity {

    public static EntityModel staticModel(int[] idx, float[] vts) {
        EntityModel entity = new EntityModel();
        entity.getRigidBody()
                .setMass(0)
                .setCollisionShape(new TriangleMeshShape(idx, vts));
        return entity;
    }

    public static EntityModel dynHull(float[] vts) {
        EntityModel entity = new EntityModel();
        entity.getRigidBody()
                .setCollisionShape(new ConvexHullShape(QuickHull.quickHull(vts)));
        return entity;
    }


}
