package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.init.Textures;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;

import java.util.Arrays;

import static java.lang.Float.NaN;

public class GuiTestWindowWidgets extends Gui {

    {
        addChildren(
          new GuiColumn().exec(g -> g.setRelativeXY(2,2)).addChildren(
            new GuiButton("Text").exec(g -> {
                g.addOnClickListener(e -> {
                    Log.LOGGER.info("OnClick");
                });
            }),
            new GuiCheckBox("CheckBox"),
            new GuiComboBox().exec((GuiComboBox g) -> {
                g.getOptions().add(new GuiText("Option001"));
                g.getOptions().add(new GuiText("Option.002"));
                g.getOptions().add(new GuiText("Op03"));
                g.getOptions().add(new GuiText("Forth Option."));
            }),
            new GuiRow().addChildren(
                new GuiRadioButton("RadBtn-01"),
                new GuiRadioButton("RB2"),
                new GuiRadioButton("BTN3")
            ),
            new GuiSwitch(),
            new GuiSlider().exec((GuiSlider g) -> {
                g.setUserMinMaxValue(0, 100);
                g.getUserOptionalValues().addAll(Arrays.asList(25f, 50f, 75f));
            }),
            new GuiTextBox("GuiTextBox"),
            new GuiScrollPanel().exec((GuiScrollPanel g) -> {
                g.setContent(new Gui(0, 0, 500, 500) {{
                    addOnDrawListener(e -> drawTexture(Textures.FLOOR, this));
                }});
                g.setClipChildren(true);
                g.setWidth(200);
                g.setHeight(200);
            })
          )
        );
    }

}
