package outskirts.event.client.input;

import outskirts.event.Event;

public class MouseMoveEvent extends Event {

    private float mouseDX;
    private float mouseDY;

    public MouseMoveEvent(float mouseDX, float mouseDY) {
        this.mouseDX = mouseDX;
        this.mouseDY = mouseDY;
    }

    public float getMouseDX() {
        return mouseDX;
    }

    public float getMouseDY() {
        return mouseDY;
    }
}
