package outskirts.client.gui;

import outskirts.event.EventPriority;
import outskirts.util.vector.Vector2f;

public class GuiLayoutLinear extends Gui {

    private Vector2f direction = new Vector2f();
    private Vector2f interval = new Vector2f();

    public GuiLayoutLinear(Vector2f direction) {
        this(direction, Vector2f.ZERO);
    }

    public GuiLayoutLinear(Vector2f direction, Vector2f interval) {
        getDirection().set(direction);
        getInterval().set(interval);

        addOnLayoutListener(e -> {
            float dx = 0, dy = 0;
            for (Gui g : getChildren()) {
                g.setRelativeXY(dx, dy);

                dx += direction.x * g.getWidth() + interval.x;
                dy += direction.y * g.getHeight() + interval.y;
            }
        });
    }

    public Vector2f getDirection() {
        return direction;
    }

    public Vector2f getInterval() {
        return interval;
    }
}
