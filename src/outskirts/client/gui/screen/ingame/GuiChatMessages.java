package outskirts.client.gui.screen.ingame;

import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiScroll;
import outskirts.client.gui.GuiText;
import outskirts.util.Colors;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

public class GuiChatMessages extends Gui {

    private Gui listMessages = new Gui().addLayoutorLayoutLinear(Vector2f.UNIT_Y).addLayoutorWrapChildren();
    {
        addGui(new GuiScroll().setContentGui(listMessages)).setWidth(300).setHeight(100).setClipChildren(true);
    }


    public void printMessage(String message) {
        listMessages.addGui(new GuiText()
                .setText(message)
                .setHeight(GuiText.DEFAULT_TEXT_HEIGHT*10)
                .addOnDrawListener(e -> {
                    drawRect(Colors.BLACK40, e.gui().getX(), e.gui().getY(), 300, e.gui().getHeight());
                })
        );
    }

}
