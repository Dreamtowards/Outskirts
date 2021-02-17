package outskirts.storage.dst;

import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.*;
import java.util.function.Consumer;

public class DArray<E> implements List<E> {

    private List<E> ls;

    public DArray() {
        this(new ArrayList<>());
    }

    public DArray(List ls) {
        this.ls = ls;
    }

    public static List fromVector3f(Vector3f vec) {
        return Arrays.asList(vec.x, vec.y, vec.z);
    }
    public static Vector3f toVector3f(List<Float> l, Vector3f dest) {
        assert l.size() == 3;
        if (dest == null) dest = new Vector3f();
        return dest.set(l.get(0), l.get(1), l.get(2));
    }

    public static List fromVector4f(Vector4f vec) {
        return Arrays.asList(vec.x, vec.y, vec.z, vec.w);
    }
    public static Vector4f toVector4f(List<Float> l, Vector4f dest) {
        assert l.size() == 4;
        if (dest == null) dest = new Vector4f();
        return dest.set(l.get(0), l.get(1), l.get(2), l.get(3));
    }

    public static List fromMatrix3f(Matrix3f m) {
        return Arrays.asList(m.m00, m.m01, m.m02,
                             m.m10, m.m11, m.m12,
                             m.m20, m.m21, m.m22);
    }
    public static Matrix3f toMatrix3f(List<Float> l, Matrix3f dest) {
        assert l.size() == 9;
        if (dest == null) dest = new Matrix3f();
        return dest.set(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5), l.get(6), l.get(7), l.get(8));
    }

    public String getString(int i) {
        return (String)get(i);
    }


    @Override
    public int size() {
        return ls.size();
    }
    @Override
    public boolean isEmpty() {
        return ls.isEmpty();
    }
    @Override
    public boolean contains(Object o) {
        return ls.contains(o);
    }
    @Override
    public Iterator<E> iterator() {
        return ls.iterator();
    }
    @Override
    public Object[] toArray() {
        return ls.toArray();
    }
    @Override
    public boolean add(E o) {
        assert size()==0 || DST.type(get(0)) == DST.type(o) : "Illegal type. the array only can holds same type elements.";
        return ls.add(o);
    }
    @Override
    public boolean remove(Object o) {
        return ls.remove(o);
    }
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return ls.addAll(c);
    }
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return ls.addAll(index, c);
    }
    @Override
    public void clear() {
        ls.clear();
    }
    @Override
    public E get(int index) {
        return ls.get(index);
    }
    @Override
    public E set(int index, E element) {
        return ls.set(index, element);
    }
    @Override
    public void add(int index, E element) {
        ls.add(index, element);
    }
    @Override
    public E remove(int index) {
        return ls.remove(index);
    }
    @Override
    public int indexOf(Object o) {
        return ls.indexOf(o);
    }
    @Override
    public int lastIndexOf(Object o) {
        return ls.lastIndexOf(o);
    }
    @Override
    public ListIterator<E> listIterator() {
        return ls.listIterator();
    }
    @Override
    public ListIterator<E> listIterator(int index) {
        return ls.listIterator(index);
    }
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return ls.subList(fromIndex, toIndex);
    }
    @Override
    public boolean retainAll(Collection c) {
        return ls.retainAll(c);
    }
    @Override
    public boolean removeAll(Collection c) {
        return ls.removeAll(c);
    }
    @Override
    public boolean containsAll(Collection c) {
        return ls.containsAll(c);
    }
    @Override
    public Object[] toArray(Object[] a) {
        return ls.toArray(a);
    }
}
