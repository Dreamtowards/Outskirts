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
