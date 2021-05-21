package general.lang;


class Collection<E> extends Iterable<E> {

    public abstract int size();

    public abstract boolean contains(E e);

    public abstract Iterator<E> iterator();

    public abstract boolean add(E e);

    public abstract boolean remove(E e);

    public abstract void clear();

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    // public abstract array<E> toArray(array<E> dest);


    public final boolean isEmpty() {
        return size() == 0;
    }

    public final boolean containsAll(Collection<E> c) {
        for (E e : c) {
            if (!contains(e))
                return false;
        }
        return true;
    }

    public final boolean addAll(Collection<E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e))
                modified = true;
        }
        return modified;
    }

    public final boolean removeAll(Collection<E> c) {
        boolean modified = false;
        for (E e : c) {
            if (remove(e))
                modified = true;
        }
        return modified;
    }

    public final boolean removeIf(Predicate<E> filter) {
        boolean removed = false;
        Iterator<E> itr = iterator();
        while (itr.hasNext()) {
            if (filter.test(itr.next())) {
                itr.remove();
                removed = true;
            }
        }
        return removed;
    }
}