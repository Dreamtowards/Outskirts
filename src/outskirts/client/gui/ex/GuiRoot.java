package outskirts.client.gui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.screen.GuiScreen;

public final class GuiRoot extends Gui {

    private GuiScreen currentScreen;  // top screen

    public GuiScreen currentScreen() {
        return currentScreen;
    }

    @Override
    public <T extends Gui> T addGui(T gui, int index) {
        return _AndUpdateCurrentScreen(super.addGui(gui, index));
    }

    @Override
    public <T extends Gui> T removeGui(int index) {
        return _AndUpdateCurrentScreen(super.removeGui(index));
    }

    private <T> T _AndUpdateCurrentScreen(T param) {
        GuiScreen g = null;
        for (int i = getChildCount()-1;i >= 0;i--) {
            if (getGui(i) instanceof GuiScreen) {
                g = getGui(i);
                break;
            }
        }
        currentScreen = g;
        Outskirts.setMouseGrabbed(currentScreen()==null);
        return param;
    }

    public GuiScreen closeScreen() {
        int i = getChildren().lastIndexOf(currentScreen());
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
