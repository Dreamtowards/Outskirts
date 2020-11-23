package outskirts.client.gui.compoents;

import outskirts.client.gui.Gui;
import outskirts.event.EventHandler;
import outskirts.util.Colors;

public class GuiScreenMapView extends Gui {

    public static GuiScreenMapView INSTANCE = new GuiScreenMapView();

    private GuiMap map;

    public GuiScreenMapView() {
        setWidth(INFINITY);
        setHeight(INFINITY);

        addOnDrawListener(this::onDlaw);
        Gui.initEscClose(this);

        addChildren(
                map=new GuiMap().exec(g -> {
                g.addLayoutorAlignParentLTRB(0, 96, 0, 96);
            })
        );
    }

    @EventHandler
    private void onDlaw(OnDrawEvent e) {
        drawRect(Colors.BLACK40, this);
        drawRect(Colors.BLACK40, map);
        drawString("MapView", getX()+100, getY()+32, Colors.WHITE, 32);

    }
}
