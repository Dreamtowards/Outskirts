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
     * Ray equation. X = P + t*R.  X: Intersection-Point. R: raydir, P: raypos.
     * @param raypos,raydir Identity Transform Space. localspace.
     * @param t the t term in Ray equation.
     * @param ndest extra hit-normal info dest-vec. null == not needed.  TEMP_EDIT. may later needs vertice info.? just now needs norm.
     * @return does ray intersected.
     */
    boolean raycast(Vector3f raypos, Vector3f raydir, Val t, Vector3f ndest);

    // SHOULD NOT OVERRIDE.
    default boolean raycast(Vector3f raypos, Vector3f raydir, Val t) {
        return raycast(raypos, raydir, t, null);
    }
}
