package outskirts.util;

import java.util.*;
import java.util.function.Consumer;

/**
 * this can make dynamic write(add/set/remove) when <Iterable> iterating
 */
// Iterate Unsafe: while iterating, element may wrongly been null.
// (as element been modified/removed, and another iterate been started async)
public class CopyOnIterateArrayList<E> extends ArrayList<E> implements RandomAccess {

    private E[] tmpItrArray = (E[])new Object[16];

    public CopyOnIterateArrayList() { }

    public CopyOnIterateArrayList(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public Iterator<E> iterator() {

        // shink.
        if (tmpItrArray.length > 16 && tmpItrArray.length > size()*2) {
            tmpItrArray = (E[])new Object[size()];
        }

        // fill elements. (if size not enough, will alloc new array
        tmpItrArray = toArray(tmpItrArray);

        return new Itr(size(), tmpItrArray);
    }

    private class Itr implements Iterator<E> {

        private int cursor = 0;
        private int size;
        private E[] itrArray;

        //the tmpItrArray.length may actually bigger than list.size(), so needs actually size info
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

    @Override
    public void forEach(Consumer<? super E> action) {
        for (E e : this) {
            action.accept(e);
        }
    }
}
