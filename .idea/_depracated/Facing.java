package outskirts.util;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

public enum Facing {

    //NONE(null), //what is this...?

    NEG_X(new Vector3f(-1,  0,  0)),
    POS_X(new Vector3f( 1,  0,  0)),

    NEG_Y(new Vector3f( 0, -1,  0)),
    POS_Y(new Vector3f( 0,  1,  0)),

    NEG_Z(new Vector3f( 0,  0, -1)),
    POS_Z(new Vector3f( 0,  0,  1));

    private Vector3f direction;

    Facing(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public static Facing nearestFacing(AABB aabb, Vector3f point) {
        float[] dArr = {
                Math.abs(aabb.min.x - point.x),
                Math.abs(aabb.max.x - point.x),
                Math.abs(aabb.min.y - point.y),
                Math.abs(aabb.max.y - point.y),
                Math.abs(aabb.min.z - point.z),
                Math.abs(aabb.max.z - point.z)
        };
        int min = 0;
        for (int i = 0;i < dArr.length;i++) {
            if (dArr[i] < dArr[min])
                min = i;
        }
        return Facing.values()[min];
    }
}
