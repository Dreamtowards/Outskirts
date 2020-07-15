package outskirts.storage;


import outskirts.storage.dat.DST;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;

import java.util.*;

/**
 * An Util-Integrated HashMap.
 */

public class DataMap implements Map<String, Object> {

    private Map mp;

    public DataMap(Map mp) {
        this.mp = mp;
    }
    public DataMap() {
        this.mp = new HashMap();
    }

    public void putVector3f(String key, Vector3f vec) {
        put(key, Arrays.asList(vec.x, vec.y, vec.z));
    }
    public Vector3f getVector3f(String key, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
        List<Float> l = (List)get(key);
        return dest.set(l.get(0), l.get(1), l.get(2));
    }

    public void putMatrix3f(String key, Matrix3f mat) {
        put(key, Arrays.asList(mat.m00, mat.m01, mat.m02,
                               mat.m10, mat.m11, mat.m12,
                               mat.m20, mat.m21, mat.m22));
    }
    public Matrix3f getMatrix3f(String key, Matrix3f dest) {
        if (dest == null) dest = new Matrix3f();
        List<Float> l = (List)get(key);
        return dest.set(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5), l.get(6), l.get(7), l.get(8));
    }




    @Override
    public Object put(String key, Object value) {
        DST.type(value); // Validation
        return mp.put(key, value);
    }
    @Override
    public void putAll(Map<? extends String, ?> m) {
        for (Object o : m.values())
            DST.type(o); // Validation
        mp.putAll(m);
    }

    @Override
    public int size() {
        return mp.size();
    }
    @Override
    public boolean isEmpty() {
        return mp.isEmpty();
    }
    @Override
    public boolean containsKey(Object key) {
        return mp.containsKey(key);
    }
    @Override
    public boolean containsValue(Object value) {
        return mp.containsValue(value);
    }
    @Override
    public Object get(Object key) {
        return mp.get(key);
    }
    @Override
    public Object remove(Object key) {
        return mp.remove(key);
    }
    @Override
    public void clear() {
        mp.clear();
    }
    @Override
    public Set<String> keySet() {
        return mp.keySet();
    }
    @Override
    public Collection<Object> values() {
        return mp.values();
    }
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return mp.entrySet();
    }
    @Override
    public boolean equals(Object o) {
        return mp.equals(o);
    }
    @Override
    public int hashCode() {
        return mp.hashCode();
    }
}
