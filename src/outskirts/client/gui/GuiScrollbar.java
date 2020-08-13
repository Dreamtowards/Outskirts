package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.Validate;

public class GuiScrollbar extends Gui {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation;

    /** scroll percentage. [0, 1] */
    private float value;

    private float handlerSize;

    private GuiDrag handlerGui = addGui(new GuiDrag()); {
        handlerGui.addOnDrawListener(e -> {
            drawRect(Colors.WHITE, handlerGui);
        });
        handlerGui.addOnDraggingListener(e -> {
            setValue(currMouseValue());
        });
    }

    public GuiScrollbar(int orientation) {
        setOrientation(orientation);

        addMouseButtonListener(e -> {
            if (e.getButtonState() && e.getMouseButton() == 0 && isHover()) {
                handlerGui.setDragging(true);
                setValue(currMouseValue());
            }
        });

        addOnDrawListener(e -> {

            drawRect(Colors.BLACK40, this);
        });

    }

    private float currMouseValue() {
        float v = getOrientation() == HORIZONTAL ?
                (Outskirts.getMouseX() - getX() - handlerGui.getWidth()/2f) / (getWidth() - handlerGui.getWidth()) :
                (Outskirts.getMouseY() - getY() - handlerGui.getHeight()/2f) / (getHeight() - handlerGui.getHeight());
        return Maths.clamp(v, 0.0f, 1.0f);
    }

    public int getOrientation() {
        return orientation;
    }
    public void setOrientation(int orientation) {
        Validate.isTrue(orientation == HORIZONTAL || orientation == VERTICAL, "Illegal orientation.");
        this.orientation = orientation;
    }

    public float getValue() {
        return value;
    }
    public void setValue(float value) {
        this.value = value;
        if (getOrientation() == HORIZONTAL) {
            handlerGui.setWidth(getWidth() * getHandlerSize());
            handlerGui.setHeight(getHeight());
            handlerGui.setRelativeX(getValue() * (getWidth() - handlerGui.getWidth()));
            handlerGui.setRelativeY(0);
        } else {
            handlerGui.setWidth(getWidth());
            handlerGui.setHeight(getHeight() * getHandlerSize());
            handlerGui.setRelativeX(0);
            handlerGui.setRelativeY(getValue() * (getHeight() - handlerGui.getHeight()));
        }
    }

    public float getHandlerSize() {
        return handlerSize;
    }
    public void setHandlerSize(float handlerSize) {
        this.handlerSize = handlerSize;
    }
}
