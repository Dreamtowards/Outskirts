package outskirts.util;

import outskirts.client.Outskirts;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.Octree;

import javax.annotation.Nullable;

// have not really implement now yet
public class RayPicker {

    private int iterateCount = 100;
    private float rayLength = 10;
    private int precisionRecursiveCount = 10;

    private Vector3f currentPoint;
    private Vector3f currentPrevPoint; //the point out of AABB
    private Vector3f currentPrecisionPoint;

    private Octree currentOctree;

    public final void update() {
        Vector3f eulerAngles = Outskirts.getCamera().getOwnerEntity().getEulerAngles();
        Vector3f cameraRay = Maths.calculateEulerDirection(eulerAngles.x, eulerAngles.y);

        Vector3f origin = Outskirts.getCamera().getOwnerEntity().getPosition();

        this.update(origin, cameraRay);
    }

    private void update(Vector3f origin, Vector3f ray) {
        Vector3f stepVector = new Vector3f(ray).scale(rayLength / iterateCount); //iterate step
        Vector3f point = new Vector3f(origin);

        AABB collidedAABB = null;

        currentPoint = null;
        currentPrevPoint = null;
        currentOctree = null;

        AABB tmpAabb = new AABB(); // Octree_AABB_TRANS

        AABB rayRange = new AABB(point, new Vector3f(point).addScaled(rayLength, ray)); // for entities get

        for (int i = 0;i < iterateCount;i++) {
            point.add(stepVector);

            Octree octree = Outskirts.getWorld().getOctree(Maths.floor(point.x), Maths.floor(point.y), Maths.floor(point.z), 4);
            if (octree != null) {
                try {
                    Octree.forChildren(octree, child -> {
                        if (child.hasBody() && child.getAABB(tmpAabb).contains(point)) {
                            currentOctree = child;
                            throw QuickExitException.INSTANCE;
                        }
                    });
                } catch (QuickExitException s) {
                    collidedAABB = new AABB(tmpAabb);
                    break;
                }
            }

            //getEntities()

        }

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

    @Nullable
    public Octree getCurrentOctree() {
        return currentOctree;
    }
}
