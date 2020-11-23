package outskirts.client.gui.screen;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.event.Events;
import outskirts.event.client.input.KeyboardEvent;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;

import static java.lang.Float.NaN;

public class GuiScreenPause extends Gui {

    public static final GuiScreenPause INSTANCE = new GuiScreenPause();

    private GuiScreenPause() {
//        setWidth(200);
//        setHeight(800);
//        addLayoutorAlignParentRR(1, 0.5f);

        addKeyboardListener(e -> {
            if (e.getKeyState() && e.getKey() == GLFW.GLFW_KEY_ESCAPE && Outskirts.getRootGUI().getGui(Outskirts.getRootGUI().size()-1) == this) {
                Outskirts.getRootGUI().removeGui(this);
            }
        });

        addChildren(
            new GuiColumn().exec(g -> {
                g.setRelativeX(20);
                g.setRelativeY(200);
            }).addChildren(
                new GuiButton("Back").exec(g -> {

                    g.addOnClickListener(e -> Outskirts.getRootGUI().removeGui(this));
                }),
                new GuiButton("Options").exec(g -> {

                    g.addOnClickListener(e -> Outskirts.getRootGUI().addGui(GuiScreenOptions.INSTANCE));
                }),
                new GuiButton("Disconne").exec(g -> {

                    g.addOnClickListener(e -> {
                        Outskirts.setWorld(null);
//        Outskirts.getPlayer().connection.closeChannel("Dinsconne");  //client ext.test
                        Outskirts.getRootGUI().removeAllGuis();
                        Outskirts.getRootGUI().addGui(GuiScreenMainMenu.INSTANCE);
                    });
                })
            )
        );
    }

    private static void drawPoint(Vector4f color, float x, float y) {
        drawRect(color, (int)(Outskirts.getWidth()/2 + x*50f), (int)(Outskirts.getHeight()/2 - y*50f), 2, 2);
    }

}
