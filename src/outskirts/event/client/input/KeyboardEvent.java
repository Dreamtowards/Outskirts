package outskirts.event.client.input;

import outskirts.event.Event;

public class KeyboardEvent extends Event {

    private int keyCode;
    private boolean keyState;

    public KeyboardEvent(int keyCode, boolean keyState) {
        this.keyCode = keyCode;
        this.keyState = keyState;
    }

    public int getKey() {
        return keyCode;
    }

    public boolean getKeyState() {
        return keyState;
    }

    public boolean isPressed() {
        return keyState;
    }
}
