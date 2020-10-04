package outskirts.physics.collision.shapes;

import outskirts.util.Ref;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

public interface Raycastable {

    // todo: should have a Ray struct .?
    // may also can getting the triangle.? (also the normal by vts winding.) not usage now.
    boolean raycast(Vector3f raypos, Vector3f raydir, Ref<Float> rst);

}
