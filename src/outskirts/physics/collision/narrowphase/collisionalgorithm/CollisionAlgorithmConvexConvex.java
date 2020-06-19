package outskirts.physics.collision.narrowphase.collisionalgorithm;

import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.narrowphase.collisionalgorithm.epa.Epa;
import outskirts.physics.collision.narrowphase.collisionalgorithm.gjk.Gjk;
import outskirts.util.vector.Vector3f;

import java.util.List;

import static outskirts.util.logging.Log.LOGGER;

public class CollisionAlgorithmConvexConvex extends CollisionAlgorithm {

    private Gjk gjk = new Gjk();
    private Epa epa = new Epa();

    @Override
    public void detectCollision(CollisionObject bodyA, CollisionObject bodyB, CollisionManifold manifold) {

        List<Gjk.SupportPoint> simplex = gjk.detectCollision(bodyA, bodyB);

        if (simplex == null)
            return; //false

        Epa.MTV mtv = epa.computeMTV(bodyA, bodyB, simplex);

        if (mtv == null) // when all Normal in same side in Epa. (Bad Simplex. e.g. a "Plane Simplex".
            return; //false

        if (mtv.penetration <= 0f) {  // when ABCDCenter close to a face in the Simplex. fpErr make wrong Normal side in Epa. (Bad Simplex. e.g. a "Plane Simplex".
            if (mtv.penetration < -0.001f) LOGGER.info("Epa penetration < 0. {}", mtv.penetration);
            return; //false
        }

        manifold.addContactPoint(new Vector3f(mtv.normal).negate(), mtv.penetration, mtv.pointOnB);


        // the ContactPoint-OnB should in both BoundingBox of bodyA and bodyB.
        // sometimes pointOnB in a "wrong" position, its in AABB of bodyB, but not in AABB of bodyA.
        // this happens when bodyB is a Huge body. the ContactPoint-OnB is Sample from the bodyB. when bodyB huge, sampling precision down.
        if (!(bodyA.getAABB().contains(mtv.pointOnB,1.8f) && bodyB.getAABB().contains(mtv.pointOnB,1.8f))) {
            LOGGER.warn("illegal ContactPoint-OnB. not in both AABB of bodyA and bodyB.");
        }

        if (mtv.penetration > 10f) {  // when wrong MTV, wrong Epa processes.
            LOGGER.warn("big penetration: {}", mtv.penetration);
        }

    }

}
