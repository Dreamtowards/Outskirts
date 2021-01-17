package outskirts.client.render.isoalgorithm.distfunc;

import outskirts.util.vector.Vector3f;

/**
 * Distance Functions
 *
 * [Inigo Quilez] https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
 */
public final class DistFunctions {

    /**
     * @param r radius.
     */
    public static float sphere(Vector3f p, float r) {
        return r - p.length();
    }

    /**
     * @param b half extent
     */
    public static float box(Vector3f p, Vector3f b) {

        return -1;
    }

}
