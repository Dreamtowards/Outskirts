package outskirts.client.gui.ex;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiDrag;
import outskirts.client.gui.GuiScrollPanel;
import outskirts.client.material.Texture;
import outskirts.event.EventPriority;
import outskirts.util.Colors;
import outskirts.util.Identifier;

/**
 * should not be a Entity Window.
 * should needs a Registry, in the "Window" title, switch to other func-window.
 */
public final class GuiWindow extends Gui {

    private static final Texture TEX_BG = Loader.loadTexture(new Identifier("textures/gui/bg/dialog_background_opaque.png").getInputStream());
    private static final Texture TEX_INDENT = Loader.loadTexture(new Identifier("textures/gui/bg/indent.png").getInputStream());
    private static final Texture TEX_SHADOW = Loader.loadTexture(new Identifier("textures/gui/shadow.png").getInputStream());

    private static final float WIN_HANDLER_HEIGHT = 32;

    public GuiWindow(Gui main) {
        setX(100);
        setY(100);
        setWidth(400);
        setHeight(400);

        {   // WindowHandler
            GuiDrag wHandler = addGui(new GuiDrag());
            wHandler.setHeight(WIN_HANDLER_HEIGHT);
            wHandler.addLayoutorAlignParentLTRB(0, 0, 0, Float.NaN);
            wHandler.addOnDraggingListener(e -> {
                setX(getX() + e.dx);
                setY(getY() + e.dy);
            });
            String title = main.getClass().getSimpleName();
            wHandler.addOnDrawListener(e -> {
//                drawRect(Colors.WHITE10, wHandler);
//                drawRect(Colors.WHITE40, wHandler.getX(), wHandler.getY(), wHandler.getWidth(), 1);
//                GuiButton.drawButtonTexture(GuiButton.TEXTURE_BUTTON_NORMAL, wHandler.getX(), wHandler.getY(), wHandler.getWidth(), wHandler.getHeight());
                drawString(title, wHandler.getX() + wHandler.getWidth() / 2, wHandler.getY() + 8, Colors.GRAY, 16, true, false);
            });
            {   // WindowClose
                Gui gClose = wHandler.addGui(new Gui());
                gClose.addLayoutorAlignParentLTRB(Float.NaN, 0, 0, 0);
                gClose.setWidth(25);
                gClose.addOnClickListener(e -> {
                    Outskirts.getRootGUI().removeGui(this);
                });
                gClose.addOnDrawListener(e -> {
                    if (gClose.isHover())
                        drawRect(Colors.WHITE20, gClose);
                    drawString("x", gClose.getX() + gClose.getWidth() / 2, gClose.getY() + 8, gClose.isHover() ? Colors.RED : Colors.GRAY, 16, true, false);
                });
            }
        }

        {   // Main Gui
            GuiScrollPanel mainPanel = addGui(new GuiScrollPanel());
            mainPanel.setContentGui(main);
            mainPanel.addLayoutorAlignParentLTRB(14, WIN_HANDLER_HEIGHT, 14, 14);
            mainPanel.setClipChildren(true);
            mainPanel.addOnDrawListener(e -> {
                drawCornerStretchTexture(TEX_INDENT, mainPanel.getX()-2, mainPanel.getY()-2, mainPanel.getWidth()+4, mainPanel.getHeight()+4, 5);
            });
        }
        {   // Resizer
            float SIZER_SZ = 8;
            GuiDrag wSizer = addGui(new GuiDrag());
            wSizer.addLayoutorAlignParentLTRB(Float.NaN, Float.NaN, -SIZER_SZ / 2f, -SIZER_SZ / 2f);
            wSizer.setWidth(SIZER_SZ);
            wSizer.setHeight(SIZER_SZ);
            wSizer.addOnDrawListener(e -> {
                if (wSizer.isHover())
                    drawRectBorder(Colors.WHITE40, wSizer, 1);
            });
            wSizer.addOnDraggingListener(e -> {
                setWidth(getWidth() + e.dx);
                setHeight(getHeight() + e.dy);
            });
        }

        addOnDrawListener(e -> {
//            drawRect(Colors.BRIGHTNESS15, this);
            drawCornerStretchTexture(TEX_BG, this, 16);
//            drawRectBorder(Colors.BLACK, this, -1);
        });

        GuiWindow.dropShadow(this, 32, 16, 2);
    }

    /**
     * @param radius radius of extra of gui bound.
     * @param yoff y offset, positives number let shadow offset 'downwards'
     * @param thin 0-1 or lite bigger. for more bigger, shadow more 'transparent'
     */
    public static void dropShadow(Gui g, float radius, float yoff, float thin) {
        g.addOnDrawListener(e -> {

            drawCornerStretchTexture(TEX_SHADOW, g.getX()-radius, g.getY()-radius+yoff, g.getWidth()+2*radius, g.getHeight()+2*radius, radius*thin);

        }).priority(EventPriority.HIGH);
    }
}
