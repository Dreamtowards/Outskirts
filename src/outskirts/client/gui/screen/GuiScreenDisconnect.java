package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.GuiButton;
import outskirts.util.Colors;
import outskirts.util.logging.Log;

//todo GuiScreenMessage/Alert/Confirm..?
public class GuiScreenDisconnect extends GuiScreen {

    private GuiButton btnBackToTitle = addGui(new GuiButton("Done")); {
        btnBackToTitle.addOnClickListener(e -> {
            Outskirts.closeScreen();
        });
        btnBackToTitle.addLayoutorAlignParentRR(0.5f, 0.6f);
    }

    public GuiScreenDisconnect(String disonnectMessage) {
        addOnDrawListener(e -> {

            drawString(disonnectMessage, Outskirts.getWidth()/2, Outskirts.getHeight()/3, Colors.WHITE);
        });
    }

}
