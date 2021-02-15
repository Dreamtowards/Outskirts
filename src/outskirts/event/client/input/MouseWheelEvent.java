package outskirts.event.client.input;

import outskirts.event.Event;

// actually sth also can scroll, even its not Mouse, like trickpad
public class MouseWheelEvent extends Event {

    private float dWheel;

    public MouseWheelEvent(float dWheel) {
        this.dWheel = dWheel;
    }

    public float getDWheel() {
        return dWheel;
    }
}
