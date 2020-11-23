package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.Texture;
import outskirts.event.EventBus;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.Validate;
import outskirts.util.logging.Log;

import java.util.function.Consumer;

public class GuiScrollbar extends Gui {

    private static final Texture TEX_SCROLL_HANDLER = Loader.loadTexture(new Identifier("textures/gui/scrollbar/ScrollBox.png").getInputStream());
    private static final Texture TEX_SCROLL_GUTTER = Loader.loadTexture(new Identifier("textures/gui/scrollbar/ScrollGutterWithBG.png").getInputStream());

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation;

    /** scroll percentage. [0, 1] */
    private float value;

    /** [0, 1] main axis size. */
    private float handlerSize = 0.1f;

    private float _OnDraggingMouseInHandlerPercen;
    private GuiDrag handlerGui = addGui(new GuiDrag()); {
        handlerGui.addOnDrawListener(e -> {
            drawCornerStretchTexture(TEX_SCROLL_HANDLER, handlerGui, 3);
        });
        handlerGui.addOnDraggingStateChangeListener(e -> {
            _OnDraggingMouseInHandlerPercen = getOrientation() == HORIZONTAL ?
                    (Outskirts.getMouseX() - handlerGui.getX()) / handlerGui.getWidth() :
                    (Outskirts.getMouseY() - handlerGui.getY()) / handlerGui.getHeight();
        });
        handlerGui.addOnDraggingListener(e -> {
            setValue(currMouseValue(_OnDraggingMouseInHandlerPercen));
        });
        handlerGui.addOnLayoutListener(e -> {
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
        });
    }

    public GuiScrollbar(int orientation) {
        setOrientation(orientation);

        addMouseButtonListener(e -> {
            if (e.getButtonState() && e.getMouseButton() == 0 && isHover()) {
                if (!handlerGui.isHover())
                    setValue(currMouseValue(0.5f));
                handlerGui.setDragging(true);
            }
        });

        addOnDrawListener(e -> {

            drawCornerStretchTexture(TEX_SCROLL_GUTTER, this, 3);
        });

    }

    private float currMouseValue(float inHandlerPercentage) {
        float v = getOrientation() == HORIZONTAL ?
                (Outskirts.getMouseX() - handlerGui.getWidth()*inHandlerPercentage - getX()) / (getWidth() - handlerGui.getWidth()) :
                (Outskirts.getMouseY() - handlerGui.getHeight()*inHandlerPercentage - getY()) / (getHeight() - handlerGui.getHeight());
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
        handlerGui.onLayout();
        performEvent(new OnValueChangedEvent());
    }

    public float getHandlerSize() {
        return handlerSize;
    }
    public void setHandlerSize(float handlerSize) {
        this.handlerSize = handlerSize;
    }

    public final EventBus.Handler addOnValueChangedListener(Consumer<OnValueChangedEvent> lsr) {
        return attachListener(OnValueChangedEvent.class, lsr);
    }

    public static class OnValueChangedEvent extends GuiEvent { }
}
