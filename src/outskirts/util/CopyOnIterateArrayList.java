package outskirts.util;

import java.util.*;

/**
 * this can make dynamic write(add/set/remove) when <Iterable> iterating
 */
public class CopyOnIterateArrayList<E> extends ArrayList<E> implements RandomAccess {

    private E[] tmpItrArray = (E[])new Object[16];

    public CopyOnIterateArrayList() { }

    public CopyOnIterateArrayList(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public Iterator<E> iterator() {

        tmpItrArray = toArray(tmpItrArray); // fill elements. (if size not enough, will alloc new array

        return new Itr(size(), tmpItrArray);
    }

    private class Itr implements Iterator<E> {

        private int cursor = 0;
        private int size;
        private E[] itrArray;

        //the tmpItrArray.length may actually bigger than list.size(), so needs size info
        private Itr(int size, E[] itrArray) {
            this.size = size;
            this.itrArray = itrArray;
        }

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public E next() {
            return itrArray[cursor++];
        }
    }
}
