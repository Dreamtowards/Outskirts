package outskirts.client.gui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

public final class GuiRoot extends Gui {

    public final void removeLastGui() {
        removeGui(count()-1);
    }

    public final Gui getLastGui() {
        return getGui(count()-1);
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

    private static Gui findHovered(Gui g) {
        if (!Gui.isMouseOver(g))
            return null;
        for (int i = g.count()-1; i >= 0; i--) {
            Gui hc = findHovered(g.getGui(i));
            if (hc != null)
                return hc;
        }
        return g;
    }


    {
        addMouseButtonListener(e -> {
            if (!Outskirts.isIngame() && e.getMouseButton() == 0 && !e.getButtonState()) {
                performOnClicks();
            }
        });
    }

    // refreshHovered() invoke from onDraw() can correctly detect when mouse-stay but ui-moving.
    // should call before do layout. cuz layout may change during refreshHovrs(). (MouseIn/Out Events..)
    public static void updateHovers() {
        Gui dst = findHovered(Outskirts.getRootGUI());
        List<Gui> hovers = new ArrayList<>();

        if (dst != null) {
            Gui.forParents(dst, g -> {
                g.setHover(true);
                hovers.add(g);
            });
        }

        Gui.forChildren(Outskirts.getRootGUI(), g -> {
            if (!hovers.contains(g))
                g.setHover(false);
        });
    }

    private static void performOnClicks() {
        Gui dst = findHovered(Outskirts.getRootGUI());
        if (dst == null)
            return;

        Gui.forParents(dst, g -> {
            if (g.isEnable() && g.isPressed()) {
                g.performEvent(new OnClickEvent());
            }
        });
    }
}
