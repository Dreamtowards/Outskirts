package outskirts.physics.collision.broadphase;

import outskirts.physics.collision.dispatch.CollisionAlgorithm;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class OverlappingPairList {

    private BroadphasePair TMP_PAIR_TRANS = BroadphasePair.of(null, null, null);

    private List<BroadphasePair> overlappingPairs = new ArrayList<>();

    public void addOverlappingPair(CollisionObject body1, CollisionObject body2) {
        //needs check group to break..?

        overlappingPairs.add(BroadphasePair.of(body1, body2, null));
    }

    public void removeOverlappingPair(CollisionObject body1, CollisionObject body2) {

        //cleanOverlappingPair(pair, dispatcher);  free algorithm

        overlappingPairs.remove(pairIndex(body1, body2));
    }

    public BroadphasePair findPair(CollisionObject body1, CollisionObject body2) {
        int i = pairIndex(body1, body2);
        if (i == -1)
            return null;
        return overlappingPairs.get(i);
    }

    private int pairIndex(CollisionObject body1, CollisionObject body2) {
        return overlappingPairs.indexOf(BroadphasePair.of(body1, body2, TMP_PAIR_TRANS));
    }

    /**
     * @param processor if return false that the pair will be remove
     */
    public void forEach(Predicate<BroadphasePair> processor) {
        for (int i = 0;i < overlappingPairs.size();) {
            BroadphasePair pair = overlappingPairs.get(i);
            if (processor.test(pair)) {
                removeOverlappingPair(pair.bodies[0], pair.bodies[1]);
                //not i++ because this item already been removed and 'next' item had fitted in this slot
            } else {
                i++;
            }
        }
    }
}
