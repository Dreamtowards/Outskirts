package outskirts.client.gui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.init.Textures;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;

import java.util.Arrays;

import static java.lang.Float.NaN;

public class GuiTestWindowWidgets extends Gui {

    {
        setWrapChildren(true);

        GuiLinearLayout linear = addGui(new GuiLinearLayout(Vector2f.UNIT_Y, new Vector2f(0, 10)));
        linear.setWrapChildren(true);
        linear.setRelativeXY(2,2);

        GuiButton button = linear.addGui(new GuiButton("Text"));
        button.addOnClickListener(e -> {
            Log.LOGGER.info("OnClick");
        });

        linear.addGui(new GuiCheckBox("CheckBox"));

        GuiComboBox comboBox = linear.addGui(new GuiComboBox());
        comboBox.getOptions().add(new GuiText("Option001"));
        comboBox.getOptions().add(new GuiText("Option.002"));
        comboBox.getOptions().add(new GuiText("Op03"));
        comboBox.getOptions().add(new GuiText("Forth Option."));

        GuiLinearLayout groupRB = linear.addGui(new GuiLinearLayout(Vector2f.UNIT_X, new Vector2f(10, 0)));
        groupRB.setWrapChildren(true);
        groupRB.addGui(new GuiRadioButton("RadBtn-01"));
        groupRB.addGui(new GuiRadioButton("RB2"));
        groupRB.addGui(new GuiRadioButton("BTN3"));

        GuiSwitch swc = linear.addGui(new GuiSwitch());

        // menu, menubar

        GuiSlider slider = linear.addGui(new GuiSlider());
        slider.setUserMinMaxValue(0, 100);
        slider.getUserOptionalValues().addAll(Arrays.asList(25f, 50f, 75f));

        linear.addGui(new GuiTextBox("GuiTextBox"));

        GuiScrollPanel scrollPanel = linear.addGui(new GuiScrollPanel());
        scrollPanel.setContentGui(new Gui(0, 0, 500, 500) {{
            addOnDrawListener(e -> drawTexture(Textures.FLOOR, this));
        }});
        scrollPanel.setClipChildren(true);
        scrollPanel.setWidth(200);
        scrollPanel.setHeight(200);
    }

}
