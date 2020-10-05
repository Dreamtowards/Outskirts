package outskirts.physics.collision.shapes;

import outskirts.util.Ref;
import outskirts.util.Val;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

public interface Raycastable {

    // todo: should have a Ray struct .?
    // getting normal.? not usage now.
    // may also can getting the triangle.? (but the like sphere, cube, just may not useful for getting triangle.
    /**
     * @param raypos,raydir Identity Transform Space. localspace.
     * @param t the t component on Ray equation.
     * @return does ray intersect.
     */
    boolean raycast(Vector3f raypos, Vector3f raydir, Ref<Float> t);

}
