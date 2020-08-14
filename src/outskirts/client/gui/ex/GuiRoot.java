package outskirts.client.gui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.screen.GuiScreen;
import outskirts.event.EventHandler;
import outskirts.event.client.input.MouseMoveEvent;
import outskirts.util.Colors;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.List;

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

    private static Gui findHoveredChild(Gui parent) {
        Gui hovered = null;
        for (Gui child : parent.getChildren()) {
            if (Gui.isMouseOver(child)) {
                hovered = child;
                Gui childHovered = findHoveredChild(child);
                if (childHovered != null)
                    hovered = childHovered;
            }
        }
        return hovered;
    }


    {
        addOnPostDrawListener(this::onPostDlaw);
    }

    @EventHandler
    private void onPostDlaw(OnPostDrawEvent event) {

        Gui hoveredGui = findHoveredChild(Outskirts.getRootGUI());
        List<Gui> hoveredGuis = new ArrayList<>();

        if (hoveredGui != null) {

            Gui.forParents(hoveredGui, g -> {
                g.setHover(true);
                hoveredGuis.add(g);
            }, true);

//        drawRect(Colors.WHITE20, hoveredGui);
//        Log.LOGGER.info("dp: "+depth(g) + ", "+g.getClass());
        }

        Gui.forChildren(Outskirts.getRootGUI(), g -> {
            if (!hoveredGuis.contains(g))
                g.setHover(false);
        }, true);

    }
}
