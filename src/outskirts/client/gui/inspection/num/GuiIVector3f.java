package outskirts.client.gui.inspection.num;

import outskirts.client.gui.Gui;
import outskirts.client.gui.stat.GuiRow;
import outskirts.util.vector.Vector3f;

public class GuiIVector3f extends Gui {

    public GuiIVector3f(Vector3f vec3) {
        setWrapChildren(true);
        addChildren(
          new GuiRow().addChildren(
            new GuiIScalar(() -> vec3.x, f->vec3.x=f),
            new GuiIScalar(() -> vec3.y, f->vec3.y=f),
            new GuiIScalar(() -> vec3.z, f->vec3.z=f)
          )
        );
    }
}
