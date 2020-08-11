package outskirts.client.gui.ui.ex;

import outskirts.client.gui.*;
import outskirts.util.vector.Vector2f;

public class GuiTestWindowWidgets extends Gui {

    {
        GuiLinearLayout linear = addGui(new GuiLinearLayout(Vector2f.UNIT_Y, new Vector2f(0, 10)));

        linear.addGui(new GuiButton("Text"));

        linear.addGui(new GuiCheckBox("CheckBox"));

        // menu, menubar

        // scroll

        linear.addGui(new GuiSlider());

        linear.addGui(new GuiTextBox("GuiTextBox"));
    }

}
