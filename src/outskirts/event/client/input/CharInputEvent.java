package outskirts.event.client.input;

import outskirts.event.Event;

public class CharInputEvent extends Event {

    private char character;

    public CharInputEvent(int character) {
        this.character = (char)character;
    }

    public char getChar() {
        return character;
    }
}
