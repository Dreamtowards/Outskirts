package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.render.lighting.Light;
import outskirts.util.Colors;

public class GuiILightsList extends Gui {

    public static GuiILightsList INSTANCE = new GuiILightsList();

    public GuiILightsList() {

        addOnDrawListener(e -> {

//            for (Light light : Outskirts.getWorld().lights) {
//
//                Gui.drawWorldpoint(light.position(), (x, y) -> {
//                    drawString("Light", x,y, Colors.WHITE);
//                });
//            }
        });

    }

}
