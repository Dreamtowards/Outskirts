package outskirts.client.render.isoalgorithm.dc;

import outskirts.util.vector.Vector3f;

import java.util.Objects;

/**
 * HermiteData of a 'sign-changed' Edge.
 */
public final class HermiteData {

    /** Intersection Point on the Edge. */
    public final Vector3f point = new Vector3f();

    /** Exact Normal of the Surface on the Intersection Point. */
    public final Vector3f norm = new Vector3f();

    public HermiteData() {}

    public HermiteData(Vector3f pv, Vector3f nv) {
        point.set(pv);
        norm.set(nv);
    }

    public HermiteData(HermiteData src) {
        point.set(src.point);
        norm.set(src.norm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HermiteData c = (HermiteData)o;
        return point.equals(c.point) && norm.equals(c.norm);
    }

    @Override
    public int hashCode() {
        return 31*point.hashCode() + norm.hashCode();
    }

    @Override
    public String toString() {
        return "HermiteData{p="+point+",n="+norm+"}";
    }
}
