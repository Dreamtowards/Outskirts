package outskirts.client.gui;

import outskirts.util.vector.Vector2i;

import java.util.Iterator;

public class GuiImmediateList<E> extends Gui {

    private Iterable<E> contentList;

    public GuiImmediateList(Iterable<E> contentList) {
        this.contentList = contentList;
    }

    @Override
    public void onDraw() {

        Vector2i offset = new Vector2i();
        Vector2i tmpItemSize = new Vector2i(); // TMP_SIZE_TRANS avoid alloc heap in loop
        for (E e : contentList) {
            tmpItemSize.set(Vector2i.ZERO);

            offset.add(onDrawItem(getX() + offset.x, getY() + offset.y, e, tmpItemSize));
        }
        setWidth(offset.x);
        setHeight(offset.y);

        super.onDraw();
    }

    public Vector2i onDrawItem(int x, int y, E item, Vector2i itemSize) {

        return itemSize;
    }
}
