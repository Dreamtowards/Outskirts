package outskirts.client.render.isoalgorithm.dc;

import outskirts.util.vector.Vector3f;

/**
 * HermiteData of a 'sign-changed' Edge.
 */
public final class HermiteData {

    /** Intersection Point on the Edge. */
    public final Vector3f point = new Vector3f();

    /** Exact Normal of the Surface on the Intersection Point. */
    public final Vector3f norm = new Vector3f();

}
