package outskirts.client.gui.ex;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiImage;
import outskirts.client.gui.inspection.num.GuiIVector3f;
import outskirts.client.gui.inspection.num.GuiIVector4f;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.client.render.Texture;
import outskirts.client.render.renderer.EntityRenderer;
import outskirts.client.render.renderer.RenderEngine;
import outskirts.util.BitmapImage;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class GuiColorPicker extends Gui {

    private static BitmapImage IMG_PALETTE = Loader.loadPNG(new Identifier("textures/gui/palette.png").getInputStream());
    private static Texture TEX_PALETTE = Loader.loadTexture(IMG_PALETTE);

    private Vector4f color;

    public GuiColorPicker(Vector4f colorRef) {
        this.color = colorRef;

        addChildren(
          new GuiColumn().addChildren(
            new GuiRow().addChildren(
              new GuiImage(TEX_PALETTE).exec(g -> {
                  g.setWidth(100);
                  g.setHeight(100);
                  g.addOnDrawListener(e -> {
                      if (g.isHover()) {
                          int pX = (int)(((Outskirts.getMouseX() - g.getX()) / g.getWidth()) * IMG_PALETTE.getWidth());
                          int pY = (int)(((Outskirts.getMouseY() - g.getY()) / g.getHeight()) * IMG_PALETTE.getHeight());
                          Vector4f col = Colors.fromRGBA(IMG_PALETTE.getPixel(pX, pY), null);
//                          if (Outskirts.isAltKeyDown())
                          drawRect(col, Outskirts.getMouseX(), Outskirts.getMouseY() - 16, 8, 8);
                          if (Outskirts.isMouseDown(0) && g.isPressed())
                              getColor().set(col);
                      }
                  });
              }),
              new Gui(0, 0, 10, 0),
              new Gui().exec(g -> {
                  g.setWidth(45);
                  g.setHeight(45);
                  g.setRelativeX(10);
                  g.addOnDrawListener(e -> {
                      drawRectBorder(Colors.BLACK, g, 2f);
                      drawRect(getColor(), g);
                  });
              })
            ),
            new GuiIVector4f(color).exec((GuiIVector4f g) -> {
                Arrays.asList(g.getScalars()).forEach(gs -> {
                    gs.setValueFilter(f -> Maths.clamp(f, 0.0f, 1.0f));
                    gs.setDragSensitivity(0.01f);
                });
            })
          )
        );

    }

    public Vector4f getColor() {
        return color;
    }
}
