package outskirts.client.gui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.debug.GuiDebugV;
import outskirts.client.gui.debug.GuiVert3D;
import outskirts.client.gui.screen.GuiScreenChat;
import outskirts.client.gui.screen.GuiScreenPause;
import outskirts.event.EventHandler;
import outskirts.event.client.input.KeyboardEvent;
import outskirts.item.stack.ItemStack;
import outskirts.util.Colors;
import outskirts.util.logging.Log;

import static org.lwjgl.input.Keyboard.*;


public class GuiIngame extends Gui {

    public static final GuiIngame INSTANCE = new GuiIngame();

    private GuiIngame() {

        addOnDrawListener(this::onDlaw);
        addKeyboardListener(this::onKeyboard);
    }

    @EventHandler
    private void onDlaw(OnDrawEvent e) {


        // Pointer. this actually not belong Debug.
        int POINTER_SIZE = 4;
        Gui.drawRect(Colors.WHITE, Outskirts.getWidth()/2f-POINTER_SIZE/2f, Outskirts.getHeight()/2f-POINTER_SIZE/2f, POINTER_SIZE, POINTER_SIZE);

        // tmp player name.
        if (Outskirts.getWorld() != null) {
//            Gui.drawWorldpoint(Outskirts.getWorld().getEntities().get(1).getPosition(), (x, y) -> {
//                Gui.drawString(Outskirts.getPlayer().getName(), x, y, Colors.GRAY);
//            });
        }

    }

    @EventHandler
    private void onKeyboard(KeyboardEvent e) {
        if (e.getKeyState() && Outskirts.isIngame()) {
            switch (e.getKey()) {
                case KEY_ESCAPE:
                    Outskirts.getRootGUI().addGui(GuiScreenPause.INSTANCE);
                    break;
                case KEY_SLASH:
                    Outskirts.getRootGUI().addGui(GuiScreenChat.INSTANCE);
                    break;
                case KEY_V:
                    Gui.toggleVisible(GuiVert3D.INSTANCE);
                    break;
                case KEY_F3:
                    Gui.toggleVisible(GuiDebugV.INSTANCE);
                    break;
            }
        }
    }

    @Override
    public float getWidth() {
        return Outskirts.getWidth();
    }
    @Override
    public float getHeight() {
        return Outskirts.getHeight();
    }
}
