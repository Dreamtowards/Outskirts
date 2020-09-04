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

        addChildren(
          new GuiDrag().exec((GuiDrag g) -> { // Window Handler
              g.setHeight(WIN_HANDLER_HEIGHT);
              g.addLayoutorAlignParentLTRB(0, 0, 0, Float.NaN);
              g.addOnDraggingListener(e -> {
                  setX(getX() + e.dx);
                  setY(getY() + e.dy);
              });
              String title = main.getClass().getSimpleName();
              g.addOnDrawListener(e -> {
                  drawString(title, g.getX() + g.getWidth() / 2, g.getY() + 8, Colors.GRAY, 16, true, false);
              });
          }).addChildren(
            new Gui().exec(g -> { // Close Window
                g.addLayoutorAlignParentLTRB(Float.NaN, 0, 0, 0);
                g.setWidth(25);
                g.addOnClickListener(e -> {
                    Outskirts.getRootGUI().removeGui(this);
                });
                g.addOnDrawListener(e -> {
                    drawString("x", g.getX() + g.getWidth() / 2, g.getY() + 8, g.isHover() ? Colors.RED : Colors.GRAY, 16, true, false);
                });
            })
          ),
          new GuiScrollPanel().exec((GuiScrollPanel g) -> {  // mainPanel
              g.setContent(main);
              g.addLayoutorAlignParentLTRB(14, WIN_HANDLER_HEIGHT, 14, 14);
              g.addOnDrawListener(e -> {
                  drawCornerStretchTexture(TEX_INDENT, g.getX()-2, g.getY()-2, g.getWidth()+4, g.getHeight()+4, 5);
              });
          }),
          new GuiDrag().exec((GuiDrag g) -> {  // Resizer
              float SIZER_SZ = 12;
              g.addLayoutorAlignParentLTRB(Float.NaN, Float.NaN, 0, 0);
              g.setWidth(SIZER_SZ);
              g.setHeight(SIZER_SZ);
              g.addOnDrawListener(e -> {
                  if (g.isHover())
                      drawRect(Colors.WHITE40, g);
              });
              g.addOnDraggingListener(e -> {
                  setWidth(getWidth() + e.dx);
                  setHeight(getHeight() + e.dy);
              });
          })
        );

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
