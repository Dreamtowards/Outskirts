package outskirts.client.gui.screen;

import outskirts.client.gui.Gui;
import outskirts.util.Validate;

/**
 * The Singleton Root GUI.
 * Events calls this gui instance, then transfer to children all GUIs
 */
public final class GuiScreenRoot extends GuiScreen {

    private static final int IND_INGAMEGUI = 0;
    private static final int IND_CURRENTSCREEN = 1;

    public GuiScreenRoot() {
        setIngameGUI(Gui.EMPTY);
        setCurrentScreen(Gui.EMPTY);
    }

    public Gui getIngameGUI() {
        return getChildAt(IND_INGAMEGUI);
    }
    public void setIngameGUI(Gui g) {
        setGui(IND_INGAMEGUI, g);
    }

    public Gui getCurrentScreen() {
        return getChildAt(IND_CURRENTSCREEN);
    }
    public void setCurrentScreen(Gui g) {
        setGui(IND_CURRENTSCREEN, g);
    }

}
