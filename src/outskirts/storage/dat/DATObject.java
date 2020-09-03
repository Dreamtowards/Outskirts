package outskirts.storage.dat;


import outskirts.storage.dat.DST;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.*;

/**
 * An Util-Integrated HashMap.
 */

public class DATObject implements Map<String, Object> {

    private Map mp;

    public DATObject(Map mp) {
        this.mp = mp;
    }
    public DATObject() {
        this.mp = new HashMap();
    }

    public void putVector3f(String key, Vector3f vec) {
        put(key, DATArray.fromVector3f(vec));
    }
    public Vector3f getVector3f(String key, Vector3f dest) {
        return DATArray.toVector3f((List<Float>)get(key), dest);
    }

    public void putVector4f(String key, Vector4f vec) {
        put(key, DATArray.fromVector4f(vec));
    }
    public Vector4f getVector4f(String key, Vector4f dest) {
        return DATArray.toVector4f((List<Float>)get(key), dest);
    }

    public void putMatrix3f(String key, Matrix3f mat) {
        put(key, DATArray.fromMatrix3f(mat));
    }
    public Matrix3f getMatrix3f(String key, Matrix3f dest) {
        return DATArray.toMatrix3f((List<Float>)get(key), dest);
    }

    public DATArray getArray(String key) {
        return (DATArray)get(key);
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