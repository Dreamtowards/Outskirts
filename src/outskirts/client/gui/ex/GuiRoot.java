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

    // how about the convinent startScreen(GuiScreen screen) .?
    @Override
    public <T extends Gui> T addGui(T gui, int index) {
        T r = super.addGui(gui, index);
        updateMouseGrab();
        return r;
    }
    @Override
    public Gui removeGui(int index) {
        Gui r = super.removeGui(index);
        updateMouseGrab();
        return r;
    }
    private void updateMouseGrab() {
        Outskirts.setMouseGrabbed(getChildCount() == 1 && getGui(0) instanceof GuiIngame);
    }

    public final void removeLastGui() {
        removeGui(size()-1);
    }

    public GuiScreen closeScreen() {

        return null;
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
        // refreshHovered() onDraw may better effects, thats in-time, when mouse stay but ui moveing, still working good.
        addOnDrawListener(e -> {
            refreshHovered();
        });

        addMouseButtonListener(e -> {
            if (e.getMouseButton() == 0 && !e.getButtonState()) {
                performOnClickeds();
            }
        });
    }

    private static void refreshHovered() {
        Gui hoveredGui = findHoveredChild(Outskirts.getRootGUI());
        List<Gui> hoveredGuis = new ArrayList<>();

        if (hoveredGui != null) {
            Gui.forParents(hoveredGui, g -> {
                g.setHover(true);
                hoveredGuis.add(g);
            }, true);
        }

        Gui.forChildren(Outskirts.getRootGUI(), g -> {
            if (!hoveredGuis.contains(g))
                g.setHover(false);
        }, true);
    }

    private static void performOnClickeds() {
        Gui targ = findHoveredChild(Outskirts.getRootGUI());

        if (targ != null) {
            Gui.forParents(targ, g -> {
                if (g.isEnable() && g.isPressed()) {
                    g.performEvent(new OnClickEvent());
                }
            }, true);
        }
    }
}
