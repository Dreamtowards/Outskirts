package outskirts.client.gui.ex;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.screen.GuiScreenChat;
import outskirts.client.gui.screen.GuiScreenPause;
import outskirts.event.client.input.KeyboardEvent;
import outskirts.item.stack.ItemStack;
import outskirts.util.Colors;
import outskirts.util.logging.Log;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SLASH;

public class GuiIngame extends Gui {

    public static final GuiIngame INSTANCE = new GuiIngame();

    private GuiIngame() {

        addOnDrawListener(this::onRender);
        addKeyboardListener(this::onKeyboard);
    }

    private void onRender(OnDrawEvent event) {


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

    private void onKeyboard(KeyboardEvent e) {
        if (e.getKeyState() && Outskirts.isIngame()) {
            switch (e.getKey()) {
                case GLFW_KEY_ESCAPE:
                    Outskirts.getRootGUI().addGui(GuiScreenPause.INSTANCE);
                    break;
                case GLFW_KEY_SLASH:
                    Outskirts.getRootGUI().addGui(GuiScreenChat.INSTANCE);
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
