package outskirts.util;

import outskirts.client.Outskirts;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

import javax.annotation.Nullable;

// have not really implement now yet
public class RayPicker {

    private int iterateCount = 100;
    private float rayLength = 10;
    private int precisionRecursiveCount = 10;

    private Vector3f currentPoint;
    private Vector3f currentPrevPoint; //the point out of AABB
    private Vector3f currentPrecisionPoint;

    public final void update() {
        this.update(Outskirts.getCamera().getPosition(), Outskirts.getCamera().getCameraUpdater().getDirection());
    }

    private void update(Vector3f origin, Vector3f ray) {
        Vector3f stepVector = new Vector3f(ray).scale(rayLength / iterateCount); //iterate step
        Vector3f point = new Vector3f(origin);

        AABB collidedAABB = null;

        currentPoint = null;
        currentPrevPoint = null;
        // todo LOTS CTT


        if (collidedAABB != null) {
            currentPoint = point;
            currentPrevPoint = new Vector3f(point).sub(stepVector);
            currentPrecisionPoint = binarySearch(collidedAABB, currentPoint, currentPrevPoint, precisionRecursiveCount);

        }
    }

    private static Vector3f binarySearch(AABB aabb, Vector3f inner, Vector3f outer, int counter) {
        Vector3f center = new Vector3f(inner).add(new Vector3f(outer).sub(inner).scale(0.5f));

        if (counter-- == 0) {
            return center;
        }

        if (aabb.contains(center)) {
            return binarySearch(aabb, center, outer, counter);
        } else {
            return binarySearch(aabb, inner, center, counter);
        }
    }

    public Vector3f getCurrentPrevPoint() {
        return currentPrevPoint;
    }

}
