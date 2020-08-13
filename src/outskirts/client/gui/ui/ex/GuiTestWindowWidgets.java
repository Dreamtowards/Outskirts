package outskirts.client.gui.ui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.init.Textures;
import outskirts.util.vector.Vector2f;

import static java.lang.Float.NaN;

public class GuiTestWindowWidgets extends Gui {

    {
        setWrapChildren(true);

        GuiLinearLayout linear = addGui(new GuiLinearLayout(Vector2f.UNIT_Y, new Vector2f(0, 10)));
        linear.setWrapChildren(true);

        linear.addGui(new GuiButton("Text"));

        linear.addGui(new GuiCheckBox("CheckBox"));

        linear.addGui(new GuiComboBox());

        GuiLinearLayout groupRB = linear.addGui(new GuiLinearLayout(Vector2f.UNIT_X));
        groupRB.addGui(new GuiRadioButton());
        groupRB.addGui(new GuiRadioButton());
        groupRB.addGui(new GuiRadioButton());

        GuiScrollbar gScrollbarH = linear.addGui(new GuiScrollbar(GuiScrollbar.HORIZONTAL));
        gScrollbarH.setWidth(200);
        gScrollbarH.setHeight(10);
        GuiScrollbar gScrollbarV = linear.addGui(new GuiScrollbar(GuiScrollbar.VERTICAL));
        gScrollbarV.setWidth(10);
        gScrollbarV.setHeight(200);
        gScrollbarV.addOnLayoutListener(e -> {
            gScrollbarV.setHeight(Outskirts.getHeight() * 0.2f);
        });

        GuiScrollPanel scrollPanel = linear.addGui(new GuiScrollPanel());
        scrollPanel.setContentGui(new Gui(0, 0, 500, 500) {{
            addOnDrawListener(e -> drawTexture(Textures.FLOOR, this));
        }});
        scrollPanel.setClipChildren(true);
        scrollPanel.setWidth(200);
        scrollPanel.setHeight(200);

        // menu, menubar

        // scroll

        linear.addGui(new GuiSlider());

        linear.addGui(new GuiTextBox("GuiTextBox"));
    }

}
