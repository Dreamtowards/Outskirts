package outskirts.event.client.input;

import outskirts.event.Event;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class KeyboardEvent extends Event {

    private int keyCode;
    private int keyState;

    public KeyboardEvent(int keyCode, int keyState) {
        this.keyCode = keyCode;
        this.keyState = keyState;
    }

    public int getKey() {
        return keyCode;
    }

    public boolean getKeyState() {
        return keyState == GLFW_PRESS;
    }
}
