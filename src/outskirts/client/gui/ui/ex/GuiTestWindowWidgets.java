package outskirts.client.gui.ui.ex;

import outskirts.client.gui.*;
import outskirts.util.vector.Vector2f;

public class GuiTestWindowWidgets extends Gui {

    {
        GuiLinearLayout linear = addGui(new GuiLinearLayout(Vector2f.UNIT_Y, new Vector2f(0, 10)));

        linear.addGui(new GuiButton("Text"));

        linear.addGui(new GuiCheckBox("CheckBox"));

        linear.addGui(new GuiComboBox());

        GuiLinearLayout groupRB = linear.addGui(new GuiLinearLayout(Vector2f.UNIT_X));
        groupRB.addGui(new GuiRadioButton());
        groupRB.addGui(new GuiRadioButton());
        groupRB.addGui(new GuiRadioButton());

        GuiScrollbar gScrollbarH = linear.addGui(new GuiScrollbar(GuiScrollbar.HORIZONTAL));
        gScrollbarH.setWidth(200);
        gScrollbarH.setHeight(20);
        gScrollbarH.setHandlerSize(0.2f);
        GuiScrollbar gScrollbarV = linear.addGui(new GuiScrollbar(GuiScrollbar.VERTICAL));
        gScrollbarV.setWidth(20);
        gScrollbarV.setHeight(200);
        gScrollbarV.setHandlerSize(0.2f);

        // menu, menubar

        // scroll

        linear.addGui(new GuiSlider());

        linear.addGui(new GuiTextBox("GuiTextBox"));
    }

}
