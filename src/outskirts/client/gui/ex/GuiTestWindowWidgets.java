package outskirts.client.gui.ex;

import outskirts.client.gui.*;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;

public class GuiTestWindowWidgets extends Gui {

    {
        GuiLayoutLinear linear = addGui(new GuiLayoutLinear(Vector2f.UNIT_Y, new Vector2f(0, 10)));

        linear.addGui(new GuiButton("Text"));

        linear.addGui(new GuiCheckBox("CheckBox"));

        // menu, menubar

        // scroll

        linear.addGui(new GuiSlider());

        linear.addGui(new GuiTextBox("GuiTextBox"));
    }

}
