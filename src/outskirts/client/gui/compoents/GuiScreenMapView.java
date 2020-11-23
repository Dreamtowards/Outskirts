package outskirts.client.gui.compoents;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiCheckBox;
import outskirts.client.gui.inspection.num.GuiIScalar;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.client.render.renderer.map.MapRenderer;
import outskirts.event.EventHandler;
import outskirts.util.Colors;

import static outskirts.util.logging.Log.LOGGER;

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
            }),
            new GuiColumn().exec(g -> {
                g.addLayoutorAlignParentLTRB(NaN, 16, 32, NaN);
            }).addChildren(
                    new GuiCheckBox("Height Clip .UDR"),
                    new GuiCheckBox("Active Chunks INFO.")
            )
        );

        addOnDetachListener(e -> {
            LOGGER.info("Detach");
        });
        addOnAttachListener(e -> {
            LOGGER.info("ATTACH");
        });
    }

    @EventHandler
    private void onDlaw(OnDrawEvent e) {
        drawRect(Colors.BLACK80, this);
        drawRect(Colors.BLACK80, map);
        drawString("MapView", getX()+100, getY()+32, Colors.WHITE, 32);

    }
}
