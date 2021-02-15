package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.util.*;

// GuiScrollBox
public class GuiScrollPanel extends Gui implements Gui.Contentable {

    private static float MOUSE_SENSTIVITY = 2.5f;
    private static float SCROLLBAR_THICKNESS = 10;

    private Gui contentw = addGui(new Gui()); // contentGui wrapper

    private GuiScrollbar hScrollbar = addGui(new GuiScrollbar(GuiScrollbar.HORIZONTAL));
    private GuiScrollbar vScrollbar = addGui(new GuiScrollbar(GuiScrollbar.VERTICAL)); {
        hScrollbar.addOnValueChangedListener(e -> {
            Gui gContent = getContent();
            if (gContent.getWidth() > getWidth())
                gContent.setRelativeX(hScrollbar.getValue() * -(gContent.getWidth()-getWidth()));
        });
        vScrollbar.addOnValueChangedListener(e -> {
            Gui gContent = getContent();
            if (gContent.getHeight() > getHeight())
                gContent.setRelativeY(vScrollbar.getValue() * -(gContent.getHeight()-getHeight()));
        });
    }

    public GuiScrollPanel() {
        setClipChildren(true);
        setContent(new Gui());

        addOnLayoutListener(e -> {
            // Position
            hScrollbar.setWidth(getWidth()-SCROLLBAR_THICKNESS);
            hScrollbar.setHeight(SCROLLBAR_THICKNESS);
            hScrollbar.setRelativeXY(0, getHeight() - hScrollbar.getHeight());

            vScrollbar.setWidth(SCROLLBAR_THICKNESS);
            vScrollbar.setHeight(getHeight());
            vScrollbar.setRelativeXY(getWidth() - vScrollbar.getWidth(), 0);

            Gui gContent = getContent();
            // Handler Size
            hScrollbar.setHandlerSize(gContent.getWidth() < getWidth() ? 0 : getWidth() / gContent.getWidth());
            vScrollbar.setHandlerSize(gContent.getHeight() < getHeight() ? 0 : getHeight() / gContent.getHeight());

            // Scroll Value
            hScrollbar.setValue(gContent.getRelativeX() / -(gContent.getWidth()-getWidth()));
            vScrollbar.setValue(gContent.getRelativeY() / -(gContent.getHeight()-getHeight()));

            hScrollbar.setVisible(gContent.getWidth() > getWidth());
            vScrollbar.setVisible(gContent.getHeight() > getHeight());

            clampScrollOffset();
        });

        addMouseWheelListener(e -> {
            if (isHover()) {
                Gui gContent = getContent();

                if (Outskirts.isShiftKeyDown()) {
                    gContent.setRelativeX(gContent.getRelativeX() + Outskirts.getDWheel() * MOUSE_SENSTIVITY);
                } else {
                    gContent.setRelativeY(gContent.getRelativeY() + Outskirts.getDWheel() * MOUSE_SENSTIVITY);
                }

                clampScrollOffset();
            }
        });
    }

    private void clampScrollOffset() {
        Gui gContent = getContent();
        gContent.setRelativeX(Maths.clamp(gContent.getRelativeX(), -Math.max(gContent.getWidth() - getWidth(), 0), 0));
        gContent.setRelativeY(Maths.clamp(gContent.getRelativeY(), -Math.max(gContent.getHeight() - getHeight(), 0), 0));
    }

    @Override
    public Gui setContent(Gui g) {
        contentw.removeAllGuis();
        contentw.addGui(g);
        return this;
    }

    @Override
    public Gui getContent() {
        return contentw.getGui(0);
    }
}
