package general.util;


class List<E> extends Collection<E> {

    public abstract E get(int index);

    public abstract E set(int index, E element);

    public abstract void add(int index, E element);

    public abstract E remove(int index);

    public abstract int indexOf(E o);

    public abstract int lastIndexOf(E o);

    public abstract ListIterator<E> iterator();

    @Override
    public boolean add(E e) {
        add(size(), e);
        return true;
    }

    @Override
    public boolean remove(E o) {
        remove(indexOf(o));
        return true;
    }

    @Override
    public boolean addAll(Collection<E> c) {
        addAll(size(), c);
        return true;
    }

    public void addAll(int index, Collection<E> c) {
        for (E e : c) {
            add(index++, e);
        }
    }

    public final void replaceAll(UnaryOperator<E> operator) {
        ListIterator<E> itr = iterator();
        while (itr.hasNext()) {
            itr.set(operator.apply(itr.next()))
        }
    }

    public void sort(Comparator<E> c);



    public static <T> List<T> of(T e) {
        return new SingletonList(e);
    }
    public static <T> List<T> of(T... e) {
        return new ArrayViewList(e);
    }
    public static <T> List<T> empty() {
        return EMPTY_LIST;
    }

}