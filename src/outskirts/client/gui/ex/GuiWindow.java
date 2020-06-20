package outskirts.client.gui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiText;
import outskirts.init.Textures;
import outskirts.util.Colors;

/**
 * should not be a Entity Window.
 * should needs a Registry, in the "Window" title, switch to other func-window.
 */
public class GuiWindow extends Gui {

    private static final float WIN_HANDLER_HEIGHT = 16;

    public GuiWindow(Gui main) {
        setX(300);
        setY(300);
        setWidth(400);
        setHeight(400);
        String title = main.getClass().getSimpleName();

        {
            Gui wHandler = addGui(new Gui());
            wHandler.addLayoutorAlignParentLTRB(0, 0, 0, Float.NaN);
            wHandler.setHeight(WIN_HANDLER_HEIGHT);
            wHandler.addOnDraggingListener((dx, dy) -> {
                setX(getX()+dx);
                setY(getY()+dy);
            });
            wHandler.addOnDrawListener(e -> {
                drawRect(Colors.WHITE, wHandler);
//                GuiButton.drawButtonTexture(GuiButton.TEXTURE_BUTTON_NORMAL, wHandler.getX(), wHandler.getY(), wHandler.getWidth(), wHandler.getHeight());
                drawString(title, wHandler.getX()+wHandler.getWidth()/2, wHandler.getY(), Colors.BLACK, 16, true, false);
            });
            {
                Gui gClose = wHandler.addGui(new Gui());
                gClose.addLayoutorAlignParentLTRB(Float.NaN, 0, 0, 0);
                gClose.setWidth(12);
                gClose.addOnClickListener(e -> {
                    Outskirts.getRootGUI().removeGui(this);
                });
                gClose.addOnDrawListener(e -> {
                    if (gClose.isMouseOver())
                        drawRect(Colors.BLACK10, gClose);
                    drawString("x", gClose.getX()+gClose.getWidth()/2, gClose.getY(), gClose.isMouseOver()?Colors.RED:Colors.BLACK, 16, true, false);
                });
            }
        }
        {
            addGui(main);
            main.addLayoutorAlignParentLTRB(0, WIN_HANDLER_HEIGHT, 0, 0);
        }
        {
            float SIZER_SZ = 8;
            Gui wSizer = addGui(new Gui());
            wSizer.addLayoutorAlignParentLTRB(Float.NaN, Float.NaN, -SIZER_SZ/2f, -SIZER_SZ/2f);
            wSizer.setWidth(SIZER_SZ).setHeight(SIZER_SZ);
            wSizer.addOnDrawListener(e -> {
                if (wSizer.isMouseOver())
                    drawRectBorder(Colors.WHITE40, wSizer, 1);
            });
            wSizer.addOnDraggingListener((dx, dy) -> {
                setWidth(getWidth()+dx);
                setHeight(getHeight()+dy);
            });
        }

        addOnDrawListener(e -> {
            drawRect(Colors.BLACK40, this);
            drawRectBorder(Colors.BLACK, this, -1);
        });
    }
}
