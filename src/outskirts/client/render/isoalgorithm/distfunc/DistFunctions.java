package outskirts.client.render.isoalgorithm.distfunc;

import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static outskirts.util.Maths.clamp;
import static outskirts.util.vector.Vector3f.abs;
import static outskirts.util.vector.Vector3f.dot;

/**
 * Signed Distance Functions. SDF.
 *
 * inner < 0, surface == 0, outer > 0.
 *
 * there has a problem. solid=negative or solid=positive.
 * when solid=pos (inner>0),
 *   its can consider that Solid is have actually number amount. and outer is not amount, neg-amount.
 *   as represent as signs in a byte, 1 as solid, 0 as empty, are more intuitive.
 * when solid=neg (inner<0), there is more mathmaticly.  negatives is more Less in numercially.
 *   you can consider if you offset all num to 0+, then lesser number first as ground.
 *   ?the gradient vector directly as outer-side normal.
 *   the SDF Distance Function format.
 *   Phillp T. were uses.
 *
 * [Inigo Quilez] https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
 */
public final class DistFunctions {





    /**
     * @param r radius.
     */
    public static float sphere(Vector3f p, float r) {
        return p.length() - r;
    }

    /**
     * @param b half extent
     */
    public static float box(Vector3f p, Vector3f b) {
        Vector3f q = abs(vec3(p)).sub(b);
        return min(max(max(q.x, q.y), q.z), 0.0f) + maxv(q, 0.0f).length();
    }

    /**
     * @param r round.
     */
    public static float roundbox(Vector3f p, Vector3f b, float r) {
        return box(p, b) - r;
    }

    /**
     * @param e edge size.
     */
    public static float boundingbox(Vector3f p, Vector3f b, float e) {
        Vector3f t = abs(vec3(p)       ).sub(b);
        Vector3f q = abs(vec3(t).add(e)).sub(e);
        return min(min(
                maxv(vec3(t.x,q.y,q.z),0.0f).length() +min(max(t.x,max(q.y,q.z)),0.0f),
                maxv(vec3(q.x,t.y,q.z),0.0f).length() +min(max(q.x,max(t.y,q.z)),0.0f)),
                maxv(vec3(q.x,q.y,t.z),0.0f).length() +min(max(q.x,max(q.y,t.z)),0.0f));
    }



    public static float torus(Vector3f p, float x, float y) {
        Vector2f q = vec2(vec2(p.x,p.z).length()-x, y);
        return q.length() - y;
    }


    /**
     * @param a,b 2 points
     */
    public static float capsule(Vector3f p, Vector3f a, Vector3f b, float r) {
        Vector3f pa = vec3(p).sub(a), ba = vec3(b).sub(a);
        float h = clamp( dot(pa,ba)/dot(ba,ba), 0.0f, 1.0f );
        return pa.sub(ba.scale(h)).length() - r;
    }








    //  UTILITIES

    public static Vector3f vec3(Vector3f v) {
        return vec3(v.x, v.y, v.z);
    }
    public static Vector3f vec3(float f) {
        return vec3(f,f,f);
    }
    public static Vector3f vec3(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }
    public static Vector3f maxv(Vector3f dest, float f) {
        return dest.set(Math.max(dest.x, f),
                        Math.max(dest.y, f),
                        Math.max(dest.z, f));
    }

    private static Vector2f vec2(float x, float y) {
        return new Vector2f(x, y);
    }
}
