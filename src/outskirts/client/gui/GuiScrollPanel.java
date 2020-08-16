package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.util.*;
import outskirts.util.vector.Vector2f;

// GuiScrollBox
public class GuiScrollPanel extends Gui {

    private static float MOUSE_SENSTIVITY = 2.5f;
    private static float SCROLLBAR_THICKNESS = 10;

    private Gui contentGui;

    private GuiScrollbar hScrollbar = addGui(new GuiScrollbar(GuiScrollbar.HORIZONTAL));
    private GuiScrollbar vScrollbar = addGui(new GuiScrollbar(GuiScrollbar.VERTICAL)); {
        hScrollbar.addOnValueChangedListener(e -> {
            if (contentGui.getWidth() > getWidth())
                contentGui.setRelativeX(hScrollbar.getValue() * -(contentGui.getWidth()-getWidth()));
        });
        vScrollbar.addOnValueChangedListener(e -> {
            if (contentGui.getHeight() > getHeight())
                contentGui.setRelativeY(vScrollbar.getValue() * -(contentGui.getHeight()-getHeight()));
        });
    }

    public GuiScrollPanel() {
        setContentGui(Gui.EMPTY);

        addOnLayoutListener(e -> {
            // Position
            hScrollbar.setWidth(getWidth()-SCROLLBAR_THICKNESS);
            hScrollbar.setHeight(SCROLLBAR_THICKNESS);
            hScrollbar.setRelativeXY(0, getHeight() - hScrollbar.getHeight());

            vScrollbar.setWidth(SCROLLBAR_THICKNESS);
            vScrollbar.setHeight(getHeight()-SCROLLBAR_THICKNESS);
            vScrollbar.setRelativeXY(getWidth() - vScrollbar.getWidth(), 0);

            // Handler Size
            hScrollbar.setHandlerSize(contentGui.getWidth() < getWidth() ? 0 : getWidth() / contentGui.getWidth());
            vScrollbar.setHandlerSize(contentGui.getHeight() < getHeight() ? 0 : getHeight() / contentGui.getHeight());

            // Scroll Value
            hScrollbar.setValue(contentGui.getRelativeX() / -(contentGui.getWidth()-getWidth()));
            vScrollbar.setValue(contentGui.getRelativeY() / -(contentGui.getHeight()-getHeight()));

            hScrollbar.setVisible(contentGui.getWidth() > getWidth());
            vScrollbar.setVisible(contentGui.getHeight() > getHeight());

            clampScrollOffset();
        });

        addMouseScrollListener(e -> {
            if (isHover()) {
                Gui gContent = getContentGui();

                if (Outskirts.isShiftKeyDown()) {
                    gContent.setRelativeX(gContent.getRelativeX() + Outskirts.getDScroll() * MOUSE_SENSTIVITY);
                } else {
                    gContent.setRelativeY(gContent.getRelativeY() + Outskirts.getDScroll() * MOUSE_SENSTIVITY);
                }

                clampScrollOffset();
            }
        });
    }

    private void clampScrollOffset() {
        Gui gContent = getContentGui();
        gContent.setRelativeX(Maths.clamp(gContent.getRelativeX(), -Math.max(contentGui.getWidth() - getWidth(), 0), 0));
        gContent.setRelativeY(Maths.clamp(gContent.getRelativeY(), -Math.max(contentGui.getHeight() - getHeight(), 0), 0));
    }

    public Gui getContentGui() {
        return contentGui;
    }
    public void setContentGui(Gui g) {
        removeGui(this.contentGui);
        addGui(g, 0);
        this.contentGui = g;
    }
}
