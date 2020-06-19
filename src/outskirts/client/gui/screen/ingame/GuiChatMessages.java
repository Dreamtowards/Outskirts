package outskirts.client.gui.screen.ingame;

import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiLayoutLinear;
import outskirts.client.gui.GuiScroll;
import outskirts.client.gui.GuiText;
import outskirts.util.Colors;

public class GuiChatMessages extends Gui {

    private GuiLayoutLinear listMessages = new GuiLayoutLinear().setOrientation(GuiLayoutLinear.VERTICAL).setWidth(300);
    private GuiScroll scrollMsgs = addGui(new GuiScroll().setContentGui(listMessages)).setWidth(300).setHeight(100).setClipChildren(true);


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
