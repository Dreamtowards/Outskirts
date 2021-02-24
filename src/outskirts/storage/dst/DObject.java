package outskirts.storage.dst;


import outskirts.util.CollectionUtils;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

/**
 * An Util-Integrated HashMap.
 */

public class DObject implements Map<String, Object> {

    private Map mp;

    public DObject(Map mp) {
        this.mp = mp;
    }
    public DObject() {
        this.mp = new HashMap();
    }

    public void putVector3f(String key, Vector3f v) {
        put(key, Arrays.asList(v.x, v.y, v.z));
    }
    public Vector3f getVector3f(String key, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
        List<Float> l = getv(key);
        assert l.size() == 3;
        return dest.set(l.get(0), l.get(1), l.get(2));
    }

    public void putVector4f(String key, Vector4f v) {
        put(key, Arrays.asList(v.x, v.y, v.z));
    }
    public <T> T getVector4f(String key, Vector4f dest) {
        if (dest == null) dest = new Vector4f();
        List<Float> l = getv(key);
        assert l.size() == 4;
        return (T)dest.set(l.get(0), l.get(1), l.get(2), l.get(3));
    }

    public void putMatrix3f(String key, Matrix3f mat) {
        put(key, CollectionUtils.asList(Matrix3f.store(mat, new float[9])));
    }
    public Matrix3f getMatrix3f(String key, Matrix3f dest) {
        List<Float> l = getv(key);
        assert l.size() == 9;
        if (dest==null) dest = new Matrix3f();
        return Matrix3f.load(dest, CollectionUtils.toArrayf(l));
    }

    public <T> DArray<T> getDArray(String key) {
        return (DArray)get(key);
    }
    public DObject getDObject(String key) {
        return (DObject)get(key);
    }

    public boolean getBoolean(String k) {
        return (byte)get(k) == 1;
    }
    public void putBoolean(String k, boolean b) {
        put(k, (byte)(b ? 1 : 0));
    }

    public String getString(String k) {
        return (String)get(k);
    }

    public byte[] getByteArray(String k) {
        return (byte[])get(k);
    }
    public InputStream getByteArrayi(String k) {
        return new ByteArrayInputStream(getByteArray(k));
    }
    public void putByteArray(String k, byte[] v) {
        put(k, v);
    }

    public int getInt(String k) {
        return (int)get(k);
    }
    public void putInt(String k, int v) {
        put(k, v);
    }

    public void putLong(String k, long v) {
        put(k, v);
    }

    public float getFloat(String k) {
        return (float)get(k);
    }
    public void putFloat(String k, float v) {
        put(k, v);
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
    public <T> T getv(Object k) {
        return (T)get(k);
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
