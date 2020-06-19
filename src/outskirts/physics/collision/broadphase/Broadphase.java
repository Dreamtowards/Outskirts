package outskirts.physics.collision.broadphase;

import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;

import java.util.ArrayList;
import java.util.List;

/**
 * Broadphase Processor (persistent pairs)
 * non-persistent's feature:
 * 1. relative simpler's struct, better for reading
 * persistent's feature:
 * 1. can maintain a internal data struct, like sorted list(SAP), huge tree struc(Dbvt)
 *    do not needs recalculate those every updates/steps
 * 2. *MUST* BroadphasePair is Persistant. that handling persistant data/cache.
 */
public abstract class Broadphase {

    private List<CollisionManifold> overlappingPairs = new ArrayList<>();

    public abstract void addObject(CollisionObject body);

    public abstract void removeObject(CollisionObject body);

    /**
     * call when body's actually AABB are changed.
     * because some Broadphase had themselves struct, like Dbvt. they handing "copies" of the body's AABB.
     * when body's actually AABB changed, those related AABB volume data of those struct should be update/sync.
     */
    public void updateAABB(CollisionObject body) {}

    public abstract void calculateOverlappingPairs();

    // required Presistent Elements. the elements contains persistent data/cache. (like ContactPoint[])
    public final List<CollisionManifold> getOverlappingPairs() {
        return overlappingPairs;
    }

}
