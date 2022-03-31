package outskirts.event.client.input;

import outskirts.event.Event;

public class InputScrollEvent extends Event {

    private final float scrollDX;
    private final float scrollDY;

    public InputScrollEvent(float scrollDX, float scrollDY) {
        this.scrollDX = scrollDX;
        this.scrollDY = scrollDY;
    }

    public float getDScroll() {
        return scrollDX+scrollDY;
    }

    public float getScrollDX() {
        return scrollDX;
    }

    public float getScrollDY() {
        return scrollDY;
    }
}
