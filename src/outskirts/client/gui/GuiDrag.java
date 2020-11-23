package outskirts.client.gui;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.event.Cancellable;
import outskirts.event.EventBus;
import outskirts.event.gui.GuiEvent;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A module tool for Mouse-Dragging
 * onDragging() when MouseOver-pressed AND moving until release pressing
 *
 * mouse dragging shouldn't use this way:
 * if (isMouseOver()) position += mouse.deltaXY;
 *
 * cause f mouse move too fast, the tricking will be lose,
 * and this way f mouse out gui border, mouse will not be tricking continue.
 */
public class GuiDrag extends Gui {

    private boolean isDragging = false;

    private Predicate<Integer> draggingPredicate = b -> b == GLFW.GLFW_MOUSE_BUTTON_LEFT;

    public GuiDrag() {
        addMouseButtonListener(e -> {
            if (draggingPredicate.test(e.getMouseButton())) {
                if (e.getButtonState() && isHover()) {
                    setDragging(true);
                } else if (isDragging() && !e.getButtonState()) {
                    setDragging(false);
                }
            }
        });

        addMouseMoveListener(e -> {
            if (isDragging()) {
                performEvent(new OnDraggingEvent(Outskirts.getMouseDX(), Outskirts.getMouseDY()));
            }
        });
    }

    public boolean isDragging() {
        return isDragging;
    }
    public void setDragging(boolean dragging) {
        if (isDragging == dragging)
            return;
        if (performEvent(new OnDraggingStateChangeEvent()))
            return;
        isDragging = dragging;
    }

    public final EventBus.Handler addOnDraggingListener(Consumer<OnDraggingEvent> lsr) {
        return attachListener(OnDraggingEvent.class, lsr);
    }

    public final EventBus.Handler addOnDraggingStateChangeListener(Consumer<OnDraggingStateChangeEvent> lsr) {
        return attachListener(OnDraggingStateChangeEvent.class, lsr);
    }

    public static class OnDraggingEvent extends GuiEvent {
        public float dx, dy;
        public OnDraggingEvent(float dx, float dy) { this.dx = dx; this.dy = dy; }
    }

    public static class OnDraggingStateChangeEvent extends GuiEvent implements Cancellable { }
}
