package outskirts.client.gui.inspection.num;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiDrag;
import outskirts.client.gui.debug.GuiBasisVisual;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

import static java.lang.Float.NaN;

public class GuiIBasis extends Gui {

    public GuiIBasis(Matrix3f basis) {
        setWrapChildren(true);

        addChildren(
          new GuiBasisVisual(basis, false).exec(g -> {
              g.setWidth(80);
              g.setHeight(80);
          }),
          new GuiDrag().exec((GuiDrag g) -> {
              g.setWidth(14);
              g.setHeight(14);
              g.addLayoutorAlignParentLTRB(NaN, NaN, 2, 2);
              g.addOnDrawListener(e -> {
                  drawRect(g.isDragging() ? Colors.WHITE40 : g.isHover() ? Colors.WHITE20 : Colors.WHITE10, g);
              });
              Matrix3f tmpRX = new Matrix3f(), tmpRY = new Matrix3f(), tmpRZ = new Matrix3f();
              g.addOnDraggingListener(e -> {
                  if (Outskirts.isShiftKeyDown()) {
                      Matrix3f.rotate(Maths.toRadians(-Outskirts.getMouseDY()), Vector3f.UNIT_Z, tmpRZ);
                      Matrix3f.mul(tmpRZ, basis, basis);
                  } else {
                      Matrix3f.rotate(Maths.toRadians(Outskirts.getMouseDX()), Vector3f.UNIT_Y, tmpRY);
                      Matrix3f.rotate(Maths.toRadians(Outskirts.getMouseDY()), Vector3f.UNIT_X, tmpRX);
                      Matrix3f.mul(tmpRY, basis, basis);
                      Matrix3f.mul(tmpRX, basis, basis);
                  }
              });
          })
        );

        addOnDrawListener(e -> {
            drawRect(Colors.BLACK10, this);
        });
    }
}
