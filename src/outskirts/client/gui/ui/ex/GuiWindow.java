package outskirts.client.gui.ui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiDrag;
import outskirts.client.gui.GuiScrollPanel;
import outskirts.util.Colors;
import outskirts.util.Maths;

/**
 * should not be a Entity Window.
 * should needs a Registry, in the "Window" title, switch to other func-window.
 */
public final class GuiWindow extends Gui {

    private static final float WIN_HANDLER_HEIGHT = 32;

    public GuiWindow(Gui main) {
        setX(100+ Maths.floor((float)Math.random()*200));
        setY(100);
        setWidth(400);
        setHeight(400);
        setClipChildren(true);
        String title = main.getClass().getSimpleName();

        {
            GuiDrag wHandler = addGui(new GuiDrag());
            wHandler.addLayoutorAlignParentLTRB(0, 0, 0, Float.NaN);
            wHandler.setHeight(WIN_HANDLER_HEIGHT);
            wHandler.addOnDraggingListener(e -> {
                setX(getX()+e.dx);
                setY(getY()+e.dy);
            });
            wHandler.addOnDrawListener(e -> {
                drawRect(Colors.WHITE10, wHandler);
                drawRect(Colors.WHITE40, wHandler.getX(), wHandler.getY(), wHandler.getWidth(), 1);
//                GuiButton.drawButtonTexture(GuiButton.TEXTURE_BUTTON_NORMAL, wHandler.getX(), wHandler.getY(), wHandler.getWidth(), wHandler.getHeight());
                drawString(title, wHandler.getX()+wHandler.getWidth()/2, wHandler.getY()+8, Colors.GRAY, 16, true, false);
            });
            {
                Gui gClose = wHandler.addGui(new Gui());
                gClose.addLayoutorAlignParentLTRB(Float.NaN, 0, 0, 0);
                gClose.setWidth(25);
                gClose.addOnClickListener(e -> {
                    Outskirts.getRootGUI().removeGui(this);
                });
                gClose.addOnDrawListener(e -> {
                    if (gClose.isMouseOver())
                        drawRect(Colors.WHITE20, gClose);
                    drawString("x", gClose.getX()+gClose.getWidth()/2, gClose.getY()+8, gClose.isMouseOver()?Colors.RED:Colors.GRAY, 16, true, false);
                });
            }
        }
        {
            addGui(new GuiScrollPanel().setContentGui(main)).addLayoutorAlignParentLTRB(0, WIN_HANDLER_HEIGHT, 0, 0);
        }
        {
            float SIZER_SZ = 8;
            GuiDrag wSizer = addGui(new GuiDrag());
            wSizer.addLayoutorAlignParentLTRB(Float.NaN, Float.NaN, -SIZER_SZ/2f, -SIZER_SZ/2f);
            wSizer.setWidth(SIZER_SZ);
            wSizer.setHeight(SIZER_SZ);
            wSizer.addOnDrawListener(e -> {
                if (wSizer.isMouseOver())
                    drawRectBorder(Colors.WHITE40, wSizer, 1);
            });
            wSizer.addOnDraggingListener(e -> {
                setWidth(getWidth()+e.dx);
                setHeight(getHeight()+e.dy);
            });
        }

        addOnDrawListener(e -> {
            drawRect(Colors.BRIGHTNESS15, this);
            drawRectBorder(Colors.BLACK, this, -1);
        });
    }
}
