package outskirts.client.gui;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.util.Colors;
import outskirts.util.KeyBinding;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2i;

public class GuiDrag extends Gui {

    private boolean dragging = false;

    public GuiDrag() {

        addMouseMoveListener(e -> {
            if (isDragging()) {
            }
        });
    }

    public void onDragging(float dx, float dy) {

    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }
}
