package outskirts.event.client.input;

import outskirts.event.Event;

public class MouseButtonEvent extends Event {

    private int mouseButton;
    private boolean buttonState;

    public MouseButtonEvent(int mouseButton, boolean buttonState) {
        this.mouseButton = mouseButton;
        this.buttonState = buttonState;
    }

    public int getMouseButton() {
        return mouseButton;
    }

    public boolean getButtonState() {
        return buttonState;
    }
}
