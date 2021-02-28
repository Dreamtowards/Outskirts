package outskirts.client.gui.inspection.num;

import outskirts.client.gui.Gui;
import outskirts.client.gui.stat.GuiRow;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

public class GuiIVector4f extends Gui {

    private GuiIScalar[] gscalars = new GuiIScalar[4];

    public GuiIVector4f(Vector4f v) {
        addChildren(
            new GuiRow().addChildren(
                gscalars[0]=new GuiIScalar(() -> v.x, f -> v.x = f),
                gscalars[1]=new GuiIScalar(() -> v.y, f -> v.y = f),
                gscalars[2]=new GuiIScalar(() -> v.z, f -> v.z = f),
                gscalars[3]=new GuiIScalar(() -> v.w, f -> v.w = f)
            )
        );
    }

    public GuiIScalar[] getScalars() {
        return gscalars;
    }
}