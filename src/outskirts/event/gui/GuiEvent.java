package outskirts.event.gui;

import outskirts.client.gui.Gui;
import outskirts.event.Event;

/**
 * GuiEvent-Extension should be stay in the corresponding Gui-Class and Gui can provide tool-method to help register lambda-handler for simpler AND clear
 * cause GuiEvent always is not global event, it is belong the owner Gui's type AND instance.
 */
public class GuiEvent extends Event {

    private Gui gui;

    // strong dependencies method. then not "get" prefix.
    public <T extends Gui> T gui() {
        return (T)gui;
    }
}
