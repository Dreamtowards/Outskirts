package outskirts.util;

import java.util.AbstractList;
import java.util.ArrayList;

public class EfficientArrayList<E> extends AbstractList<E> {

    private E[] elements;

    private int size;

    @Override
    public E get(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException();
        return elements[index];
    }

    @Override
    public int size() {
        return 0;
    }
}
