package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.Validate;
import outskirts.util.vector.Vector2f;

public class GuiScroll extends Gui {

    /**
     * scroll value. xy <= 0
     */
    private Vector2f scrollOffset = new Vector2f();

    private float scrollSensitivity = 1f;

    public GuiScroll() {
        setContentGui(new Gui());

        setScrollHandlerGui(GuiScrollHandle.AXIS_X, new GuiScrollHandle(GuiScrollHandle.AXIS_X));
        setScrollHandlerGui(GuiScrollHandle.AXIS_Y, new GuiScrollHandle(GuiScrollHandle.AXIS_Y));

        addMouseScrollListener(e -> {
            if (isMouseOver()) {

                if (Outskirts.isShiftKeyDown()) {
                    scrollOffset.x += Outskirts.getDScroll() * scrollSensitivity;
                } else {
                    scrollOffset.y += Outskirts.getDScroll() * scrollSensitivity;
                }

                clampScrollOffset();
            }
        });
        addOnDrawListener(e -> {
            Gui contentGui = getContentGui();

            contentGui.setRelativeX((int)scrollOffset.x);
            contentGui.setRelativeY((int)scrollOffset.y);
        });
    }

    private void clampScrollOffset() {
        Gui contentGui = getContentGui();
        scrollOffset.x = Maths.clamp(scrollOffset.x, Math.min(getWidth() - contentGui.getWidth(), 0), 0);
        scrollOffset.y = Maths.clamp(scrollOffset.y, Math.min(getHeight() - contentGui.getHeight(), 0), 0);
    }

    public Vector2f getScrollOffset() {
        return scrollOffset;
    }

    public float getScrollSensitivity() {
        return scrollSensitivity;
    }

    public void setScrollSensitivity(float scrollSensitivity) {
        this.scrollSensitivity = scrollSensitivity;
    }

    public <T extends Gui> T setContentGui(Gui contentGui) {
        Validate.notNull(contentGui, "ContentGui can't be null");
        setGui(0, contentGui);
        return (T)this;
    }

    private void setScrollHandlerGui(int axis, Gui gui) {
        switch (axis) {
            case GuiScrollHandle.AXIS_X:
                setGui(1, gui);
                break;
            case GuiScrollHandle.AXIS_Y:
                setGui(2, gui);
                break;
            default:
                throw new IllegalArgumentException("Illegal handler axis");
        }
    }

    public <T extends Gui> T getContentGui() {
        return getChildAt(0);
    }

    public static class GuiScrollHandle extends Gui {

        public static final int AXIS_X = 0, AXIS_Y = 1;

        private int axis = AXIS_X;
        private int handleSize = 5;

        public GuiScrollHandle(int axis) {
            this.axis = axis;

            addOnDraggingListener((dx, dy) -> {
                GuiScroll owner = getParent();
                Gui contentGui = owner.getContentGui();

                if (axis == AXIS_X) {
                    float scrollRatioX = contentGui.getWidth() / owner.getWidth();
                    owner.getScrollOffset().x += -scrollRatioX * dx;
                } else if (axis == AXIS_Y) {
                    float scrollRatioY = contentGui.getHeight() / owner.getHeight();
                    owner.getScrollOffset().y += -scrollRatioY * dy;
                }

                owner.clampScrollOffset();
            });

            addOnDrawListener(e -> {
                GuiScroll owner = getParent();
                Gui contentGui = owner.getContentGui();
                if (axis == AXIS_X) {
                    float viewPercentX = owner.getWidth() / contentGui.getWidth();
                    setWidth(viewPercentX >= 1 ? 0 : Maths.ceil(owner.getWidth() * viewPercentX));
                    setHeight(handleSize);

                    float offsetPercentX = -owner.getScrollOffset().x / contentGui.getWidth();
                    setX(owner.getX() + (int)(owner.getWidth() * offsetPercentX));
                    setY(owner.getY() + owner.getHeight() - handleSize);
                } else if (axis == AXIS_Y) {
                    float viewPercentY = owner.getHeight() / contentGui.getHeight();
                    setHeight(viewPercentY >= 1 ? 0 : Maths.ceil(owner.getHeight() * viewPercentY));
                    setWidth(handleSize);

                    float offsetPercentY = -owner.getScrollOffset().y / contentGui.getHeight();
                    setY(owner.getY() + (int)(owner.getHeight() * offsetPercentY));
                    setX(owner.getX() + owner.getWidth() - handleSize);
                }
                drawRect(Colors.WHITE20, this);
            });
        }
    }
}
