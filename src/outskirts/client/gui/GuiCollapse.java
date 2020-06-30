package outskirts.client.gui;

import outskirts.util.Colors;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;

public class GuiCollapse extends Gui {

    private boolean collapsed = false;


    private Gui titleGui = addGui(new Gui());
    private Gui bodyGui = addGui(new Gui());

    {
        setHeight(16);
        setWidth(200);

        titleGui.addLayoutorWrapChildren(4,4,4,4);
        titleGui.addOnClickListener(e -> {
            collapsed = !collapsed;
            bodyGui.setVisible(!collapsed);
        });

        addLayoutorLayoutLinear(Vector2f.UNIT_Y);
        addLayoutorWrapChildren();
        addOnDrawListener(e -> {
            drawRect(Colors.WHITE20, this);
        });
    }

    public Gui getTitleGui() {
        return titleGui;
    }

    public Gui getBodyGui() {
        return bodyGui;
    }
}
