package outskirts.physics.collision.broadphase;

import outskirts.util.vector.Vector3f;

public class BoundingSphere {

    public Vector3f center;

    public float radius;

    private static Vector3f TMP_VEC_TRANS = new Vector3f();

    public boolean interescts(BoundingSphere other) {
        // intersects = (o1.center - o2.center).length() < o1.radius + or.radius
        float distanceSq = Vector3f.sub(this.center, other.center, TMP_VEC_TRANS).lengthSquared();

        float sumRadius = this.radius + other.radius;

        return distanceSq < sumRadius * sumRadius;
    }

}
