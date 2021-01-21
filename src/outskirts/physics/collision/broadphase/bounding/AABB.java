package outskirts.physics.collision.broadphase.bounding;

import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;

import java.util.Arrays;

//2 vec is more flexibly and look-better than 6 float
public class AABB {

    public static final AABB ZERO = new AABB();
    public static final AABB INFINITY = new AABB().set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
                                                       Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

    public final Vector3f min = new Vector3f();
    public final Vector3f max = new Vector3f();

    public AABB() {}

    public AABB(AABB src) {
        set(src);
    }

    public AABB(Vector3f p1, Vector3f p2) {
        set(p1, p2);
    }

    public AABB(float x1, float y1, float z1, float x2, float y2, float z2) {
        set(x1, y1, z1, x2, y2, z2);
    }

    public AABB set(AABB other) {
        return set(other.min, other.max);
    }

    public AABB set(Vector3f p1, Vector3f p2) {
        return set(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }

    public AABB set(float x1, float y1, float z1, float x2, float y2, float z2) {
        min.x = Math.min(x1, x2);
        min.y = Math.min(y1, y2);
        min.z = Math.min(z1, z2);
        max.x = Math.max(x1, x2);
        max.y = Math.max(y1, y2);
        max.z = Math.max(z1, z2);
        return this;
    }

    public boolean contains(float x, float y, float z, float e) {
        return x > min.x-e && x < max.x+e && y > min.y-e && y < max.y+e && z > min.z-e && z < max.z+e;
    }
    public final boolean contains(Vector3f p, float e) {
        return contains(p.x, p.y, p.z, e);
    }
    public final boolean contains(Vector3f p) {
        return contains(p.x, p.y, p.z, 0);
    }
    public final boolean contains(AABB aabb) {
        return contains(aabb.min) && contains(aabb.max);
    }

    public boolean containsEquals(float x, float y, float z) {
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y && z >= min.z && z <= max.z;
    }
    public final boolean containsEquals(Vector3f p) {
        return containsEquals(p.x, p.y, p.z);
    }
    public final boolean containsEquals(AABB aabb) {
        return containsEquals(aabb.min) && containsEquals(aabb.max);
    }

    // for continous checking. the point (EqGr>=Min && Ls<Max)
    public boolean containsEqLs(float x, float y, float z) {
        return x >= min.x && x < max.x && y >= min.y && y < max.y && z >= min.z && z < max.z;
    }
    public boolean containsEqLs(Vector3f p) {
        return containsEqLs(p.x, p.y, p.z);
    }

    public AABB grow(float x, float y, float z) {
        min.sub(x, y, z);
        max.add(x, y, z);
        return this;
    }
    public AABB grow(Vector3f vec) {
        return grow(vec.x, vec.y, vec.z);
    }
    public AABB grow(float f) {
        return grow(f, f, f);
    }

    public AABB expand(Vector3f vec) {
        if (vec.x < 0f) min.x += vec.x;
        else max.x += vec.x;

        if (vec.y < 0f) min.y += vec.y;
        else max.y += vec.y;

        if (vec.z < 0f) min.z += vec.z;
        else max.z += vec.z;
        return this;
    }

    // renameTo union .? merge
    public AABB include(float px, float py, float pz) {
        min.x = Math.min(min.x, px);
        min.y = Math.min(min.y, py);
        min.z = Math.min(min.z, pz);
        max.x = Math.max(max.x, px);
        max.y = Math.max(max.y, py);
        max.z = Math.max(max.z, pz);
        return this;
    }
    public AABB include(Vector3f p) {
        return include(p.x, p.y, p.z);
    }
    public AABB include(AABB aabb) {
        return include(aabb.min).include(aabb.max);
    }

    public AABB union(AABB aabb) { // not test yet
        min.x = Math.max(min.x, aabb.min.x);
        min.y = Math.max(min.y, aabb.min.y);
        min.z = Math.max(min.z, aabb.min.z);
        max.x = Math.min(max.x, aabb.max.x);
        max.y = Math.min(max.y, aabb.max.y);
        max.z = Math.min(max.z, aabb.max.z);
        // check AABB illegal. (ensure min < max
        if (min.x > max.x) min.x = max.x = 0;
        if (min.y > max.y) min.y = max.y = 0;
        if (min.z > max.z) min.z = max.z = 0;
        return this;
    }

    public AABB translate(float x, float y, float z) {
        min.add(x, y, z);
        max.add(x, y, z);
        return this;
    }
    public final AABB translate(Vector3f trans) {
        return translate(trans.x, trans.y, trans.z);
    }
    public final AABB translate(float scalar, Vector3f trans) {  // translateScaled
        return translate(trans.x*scalar, trans.y*scalar, trans.z*scalar);
    }

    private boolean intersects(AABB other, float e) {
        return  min.x < other.max.x+e && max.x > other.min.x-e &&
                min.y < other.max.y+e && max.y > other.min.y-e &&
                min.z < other.max.z+e && max.z > other.min.z-e;
    }
    public static boolean intersects(AABB a, AABB b, float e) {
        return a.intersects(b, e);
    }
    public static boolean intersects(AABB a, AABB b) {
        return a.intersects(b, 0);
    }
    
    public boolean intersectsX(AABB other) {
        return other.max.x > this.min.x && other.min.x < this.max.x;
    }

    public boolean intersectsY(AABB other) {
        return other.max.y > this.min.y && other.min.y < this.max.y;
    }

    public boolean intersectsZ(AABB other) {
        return other.max.z > this.min.z && other.min.z < this.max.z;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AABB && ((AABB)obj).min.equals(this.min) && ((AABB)obj).max.equals(this.max);
    }

    @Override
    public int hashCode() {
        return min.hashCode() * 31 ^ max.hashCode();
    }

    @Override
    public String toString() {
        return "AABB["+min+", "+max+"]";
    }




    public static AABB bounding(Vector3f[] vertices, AABB dest) {  // wrap
        if (dest == null)
            dest = new AABB();
        dest.set(vertices[0], vertices[0]);
        for (Vector3f vert : vertices) {
            dest.include(vert);
        }
        return dest;
    }
    public static AABB bounding(Iterable<Vector3f> vertices, AABB dest) {
        if (dest == null)
            dest = new AABB();
        boolean initbd = false;
        for (Vector3f vert : vertices) {
            if (!initbd) { dest.set(vert, vert);initbd=true; }
            dest.include(vert);
        }
        return dest;
    }
    public static AABB bounding(float[] vertices, AABB dest) {  // wrap
        if (dest == null)
            dest = new AABB();
        dest.set(vertices[0],vertices[1],vertices[2], vertices[0],vertices[1],vertices[2]);
        for (int i = 0;i < vertices.length/3;i++) {
            dest.include(vertices[i*3], vertices[i*3+1], vertices[i*3+2]);
        }
        return dest;
    }

    public static AABB merge(AABB aabb1, AABB aabb2, AABB dest) {
        if (dest == null)
            dest = new AABB();
        return dest.set(aabb1).include(aabb2);
    }

    public static Vector3f[] vertices(AABB aabb, Vector3f[] dest) {
        if (dest == null)
            dest = CollectionUtils.fill(new Vector3f[8], Vector3f::new);

        dest[0].set(aabb.min.x, aabb.min.y, aabb.min.z);
        dest[1].set(aabb.max.x, aabb.min.y, aabb.min.z);
        dest[2].set(aabb.max.x, aabb.min.y, aabb.max.z);
        dest[3].set(aabb.min.x, aabb.min.y, aabb.max.z);

        dest[4].set(aabb.min.x, aabb.max.y, aabb.min.z);
        dest[5].set(aabb.max.x, aabb.max.y, aabb.min.z);
        dest[6].set(aabb.max.x, aabb.max.y, aabb.max.z);
        dest[7].set(aabb.min.x, aabb.max.y, aabb.max.z);

        return dest;
    }

    // renameTo extent
    public static Vector3f extent(AABB aabb, Vector3f dest) {
        return Vector3f.sub(aabb.max, aabb.min, dest);
    }

    public static Vector3f center(AABB aabb, Vector3f dest) {
        return Vector3f.add(aabb.min, aabb.max, dest).scale(1/2f);
    }

    public static float volume(AABB aabb) {
        Vector3f szdiff = AABB.extent(aabb, null);  // STACK
        return szdiff.x*szdiff.y*szdiff.z;
    }

    public static float centdistanf(AABB aabb1, AABB aabb2) {
        return new Vector3f().add(aabb1.min).add(aabb1.max).sub(aabb2.min).sub(aabb2.max).lengthSquared();
    }
}
