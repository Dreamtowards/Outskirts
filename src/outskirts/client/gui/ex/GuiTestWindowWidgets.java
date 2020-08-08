package outskirts.client.gui.ex;

import outskirts.client.gui.*;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;

public class GuiTestWindowWidgets extends Gui {

    {
        addLayoutorLayoutLinear(new Vector2f(0, 2));

        addGui(new GuiButton("GuiButton Text"));

        addGui(new GuiCheckBox("GuiCheckBox"));

        // menu, menubar

        // scroll

        addGui(new GuiSlider());

        addGui(new GuiTextBox("GuiTextField"));
    }

}
