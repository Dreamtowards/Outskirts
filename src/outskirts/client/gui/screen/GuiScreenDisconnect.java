package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.GuiButton;
import outskirts.util.Colors;
import outskirts.util.logging.Log;

//todo GuiScreenMessage/Alert/Confirm..?
public class GuiScreenDisconnect extends GuiScreen {

    private GuiButton btnBackToTitle = addGui(new GuiButton("Done")).addOnClickListener(e -> {
        Outskirts.closeScreen();
    }).addOnDrawListener(e -> {
        e.gui().setX(Outskirts.getWidth()/2 - e.gui().getWidth()/2);
        e.gui().setY(Outskirts.getHeight()/5*3);
    });

    public GuiScreenDisconnect(String disonnectMessage) {
        addOnDrawListener(e -> {

            drawString(disonnectMessage, Outskirts.getWidth()/2, Outskirts.getHeight()/3, Colors.WHITE);
        });
    }

}
