package outskirts.client.gui;

import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiMenu;
import outskirts.client.gui.GuiText;
import outskirts.util.Colors;
import outskirts.util.vector.Vector2f;

public class GuiMenubar extends Gui {

    {
        setHeight(GuiText.DEFAULT_TEXT_HEIGHT);

        addGui(new Gui()).setWidth(12); // just take a width.
        addLayoutorLayoutLinear(Vector2f.UNIT_X);

        addOnDrawListener(e -> {
            drawRect(Colors.WHITE40, getX(), getY(), getWidth(), getHeight());
        });
    }

    public GuiMenu addMenu(String name, GuiMenu menu) {

        Gui btn = addGui(new GuiText("  "+name+"  "));
        btn.addOnClickListener(e -> {
            if (!menu.isVisible()) {
                menu.show(btn.getX(), btn.getY() + btn.getHeight());
            } else {
                menu.hide();
            }
        });
        btn.addOnDrawListener(e -> {
            if (menu.isVisible()) {
                drawRect(Colors.BLACK, btn.getX(), btn.getY(), btn.getWidth(), btn.getHeight());
            }
        });
        btn.addGui(menu); // this way.?

        return menu;
    }

}
