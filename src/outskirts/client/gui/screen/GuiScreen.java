package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;

/**
 * GuiScreen is just a mark
 */
public abstract class GuiScreen extends Gui {

    @Override
    public float getWidth() {
        return Outskirts.getWidth();
    }

    @Override
    public float getHeight() {
        return Outskirts.getHeight();
    }
}
