package outskirts.client.gui;

import outskirts.util.Colors;
import outskirts.util.vector.Vector2f;

public class GuiMenubar extends Gui {

    private GuiLayoutLinear menus = addGui(new GuiLayoutLinear(Vector2f.UNIT_X)); {
        menus.setWrapChildren(true);
        menus.setRelativeX(12); // just take a start space.
    }

    {
        setWrapChildren(true);
        setHeight(GuiText.DEFAULT_TEXT_HEIGHT);

        addOnDrawListener(e -> {
            drawRect(Colors.WHITE40, this);
        });
    }

    public GuiPopupMenu addMenu(String name, GuiPopupMenu menu) {
        Gui btn = menus.addGui(new GuiText("  "+name+"  "));
        btn.addOnClickListener(e -> {
            if (!menu.isVisible()) {
                menu.show(btn.getX(), btn.getY() + btn.getHeight());
            } else {
                menu.hide();
            }
        });
        btn.addOnDrawListener(e -> {
            if (menu.isVisible()) {
                drawRect(Colors.BLACK, btn);
            }
        });
        btn.addGui(menu); // for let menu put to the context e.g. onDraw. (but sure uses this way.?

        return menu;
    }

}
