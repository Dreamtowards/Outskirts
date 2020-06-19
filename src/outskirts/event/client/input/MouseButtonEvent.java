package outskirts.event.client.input;

import org.lwjgl.glfw.GLFW;
import outskirts.event.Event;

public class MouseButtonEvent extends Event {

    private int mouseButton;
    private int buttonState;

    public MouseButtonEvent(int mouseButton, int buttonState) {
        this.mouseButton = mouseButton;
        this.buttonState = buttonState;
    }

    public int getMouseButton() {
        return mouseButton;
    }

    public boolean getButtonState() {
        return buttonState == GLFW.GLFW_PRESS;
    }
}
