package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.event.EventBus;
import outskirts.event.gui.GuiEvent;

import java.util.function.Consumer;

public class GuiDrag extends Gui {

    private boolean isDragging = false;

    public GuiDrag() {
        addMouseButtonListener(e -> {
            if (e.getMouseButton() == 0) {
                if (e.getButtonState() && isHover()) {
                    setDragging(true);
                } else if (isDragging() && !e.getButtonState()){
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
        isDragging = dragging;
        performEvent(new OnDraggingStateChangedEvent());
    }

    public final EventBus.Handler addOnDraggingListener(Consumer<OnDraggingEvent> lsr) {
        return attachListener(OnDraggingEvent.class, lsr);
    }

    public final EventBus.Handler addOnDraggingStateChangedListener(Consumer<OnDraggingStateChangedEvent> lsr) {
        return attachListener(OnDraggingStateChangedEvent.class, lsr);
    }

    public static class OnDraggingEvent extends GuiEvent {
        public float dx, dy;
        public OnDraggingEvent(float dx, float dy) { this.dx = dx; this.dy = dy; }
    }

    public static class OnDraggingStateChangedEvent extends GuiEvent { }
}
