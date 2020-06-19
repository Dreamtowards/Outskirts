package outskirts.client.gui;

import outskirts.util.Validate;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector2i;

public class GuiLayoutLinear extends Gui {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation = HORIZONTAL;

    public GuiLayoutLinear() {

        addOnDrawListener(e -> {

            this.updateLayout();
        });
    }

    public void updateLayout() {

        //refresh children offsets
        int startX = 0;
        int startY = 0;
        for (int i = 0;i < getChildCount();i++) {
            Gui child = getChildAt(i);

            child.setRelativeX(startX);
            child.setRelativeY(startY);

            if (getOrientation() == HORIZONTAL) {
                startX += child.getWidth();
            } else if (getOrientation() == VERTICAL) {
                startY += child.getHeight();
            }
        }

        //refresh layout bound
        Vector2f layoutBound = Gui.calculateChildrenBound(this);
        setWidth(layoutBound.x);
        setHeight(layoutBound.y);
    }

    public int getOrientation() {
        return orientation;
    }

    public GuiLayoutLinear setOrientation(int orientation) {
        Validate.isTrue(orientation == VERTICAL || orientation == HORIZONTAL, "orientation must be VERTICAL or HORIZONTAL");

        this.orientation = orientation;
        return this;
    }
}
