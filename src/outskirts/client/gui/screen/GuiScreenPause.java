package outskirts.client.gui.screen;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.event.Events;
import outskirts.event.client.input.KeyboardEvent;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;

import static java.lang.Float.NaN;

public class GuiScreenPause extends Gui {

    public static final GuiScreenPause INSTANCE = new GuiScreenPause();

    private GuiScreenPause() {
        setWidth(200);
        setHeight(800);
//        addLayoutorAlignParentRR(1, 0.5f);

        addKeyboardListener(e -> {
            if (e.getKeyState() && e.getKey() == GLFW.GLFW_KEY_ESCAPE && Outskirts.getRootGUI().getGui(Outskirts.getRootGUI().size()-1) == this) {
                Outskirts.getRootGUI().removeGui(this);
            }
        });
    }

    private static void drawPoint(Vector4f color, float x, float y) {
        drawRect(color, (int)(Outskirts.getWidth()/2 + x*50f), (int)(Outskirts.getHeight()/2 - y*50f), 2, 2);
    }

    private GuiButton btnBack = addGui(new GuiButton("Back")); {
        btnBack.addOnClickListener(e -> {
            Outskirts.getRootGUI().removeGui(this);
        });
        btnBack.addLayoutorAlignParentLTRB(NaN, 100, 20, NaN);
    }

    private GuiButton btnOptions = addGui(new GuiButton("Options")); {
        btnOptions.addOnClickListener(e -> {
            Outskirts.getRootGUI().addGui(GuiScreenOptions.INSTANCE);
        });
        btnOptions.addOnLayoutListener(e -> {
            btnOptions.addLayoutorAlignParentLTRB(NaN, 160, 20, NaN);
        });
    }

    private GuiButton btnDisconne = addGui(new GuiButton("Disconne")); {
        btnDisconne.addOnClickListener(e -> {
            Outskirts.setWorld(null);
//        Outskirts.getPlayer().connection.closeChannel("Dinsconne");  //client ext.test
            Outskirts.getRootGUI().removeAllGuis();
            Outskirts.getRootGUI().addGui(GuiScreenMainMenu.INSTANCE);
        });
        btnDisconne.addOnLayoutListener(e -> {
            btnDisconne.addLayoutorAlignParentLTRB(NaN, 220, 20, NaN);
        });
    }

}
