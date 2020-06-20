package outskirts.client.gui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.screen.GuiScreen;

public class GuiRoot extends Gui {

    private GuiScreen currentScreen;  // top screen

    public GuiScreen currentScreen() {
        return currentScreen;
    }

    @Override
    public <T extends Gui> T addGui(T gui, int index) {
        return updateTopScreen(super.addGui(gui, index));
    }

    @Override
    public <T extends Gui> T removeGui(int index) {
        return updateTopScreen(super.removeGui(index));
    }

    private <T> T updateTopScreen(T param) {
        GuiScreen g = null;
        for (int i = getChildCount()-1;i >= 0;i--) {
            if (getChildAt(i) instanceof GuiScreen) {
                g = getChildAt(i);
                break;
            }
        }
        currentScreen = g;
        Outskirts.setMouseGrabbed(currentScreen()==null);
        return param;
    }

    public GuiScreen closeScreen() {
        int i = lastIndexOfGui(currentScreen());
        if (i == -1) return null;
        return removeGui(i);
    }
    public void closeAllScreen() {
        while (closeScreen() != null);
    }


    @Override
    public float getX() {
        return 0;
    }
    @Override
    public float getY() {
        return 0;
    }

    @Override
    public float getWidth() {
        return Outskirts.getWidth();
    }
    @Override
    public float getHeight() {
        return Outskirts.getHeight();
    }
}
