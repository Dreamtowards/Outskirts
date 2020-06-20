package outskirts.client.gui;

import outskirts.util.Colors;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;

import java.awt.*;
import java.util.function.Consumer;

//PopupMenu
public class GuiMenu extends Gui {

    private boolean topmenu = true;
    private long lastShowMillis;

    public GuiMenu() {
        hide();

        addLayoutorLayoutLinear(Vector2f.UNIT_Y);
        addLayoutorWrapChildren();

        // when mouse-click in outside, hide the menu.
        addMouseButtonListener(e -> {
            if (lastShowMillis+300 < System.currentTimeMillis() && !isMouseInMenu(this)) {  // rem topmenu
                hide();
            }
        });
    }

    public final void show(float x, float y) {
        setX(x).setY(y).setVisible(true);
        lastShowMillis = System.currentTimeMillis();
    }

    public final void hide() {
        setVisible(false);
    }

    private static boolean isMouseInMenu(GuiMenu menu) {
        for (int i = 0;i < menu.getChildCount();i++) {
            GuiItem item = menu.getChildAt(i);
            if (item.isMouseOver()) {
                return true;
            } else if (item.subMenu != null && item.subMenu.isVisible()) {
                return isMouseInMenu(item.subMenu);
            }
        }
        return false;
    }

    public static class GuiItem extends GuiText {

        public static GuiItem button(String text) {
            GuiItem g = new GuiItem();
            g.setText(text);
            return g;
        }

        private GuiMenu subMenu;

        public void setSubMenu(GuiMenu subMenu) {
            this.subMenu = subMenu;
            subMenu.topmenu = false;
            addGui(subMenu);
        }

        private GuiItem() {
            Consumer lsr = e -> {
                for (int i = 0;i < getParent().getChildCount();i++) {
                    GuiItem item = getParent().getChildAt(i);
                    if (item.subMenu != null) {
                        if (item.isMouseOver()) {
                            item.subMenu.setX(getX() + getWidth()).setY(getY());
                            item.subMenu.setVisible(true);
                        } else if (item.subMenu.isVisible() && !isMouseInMenu(item.subMenu)) {
                            item.subMenu.hide();
                        }
                    }
                }
            };

            addOnMouseExitedListener(lsr);
            addOnMouseEnteredListener(lsr);

            addOnDrawListener(e -> {
                Gui paren = getParent();
                if (isMouseOver()) {
                    drawRect(Colors.BLACK40, getX(), getY(), paren.getWidth(), getHeight());
                }
                drawRect(Colors.WHITE20, getX(), getY(), paren.getWidth(), getHeight());
                if (subMenu != null) {
                    drawRect(Colors.GREEN, getX() + paren.getWidth() - 5, getY(), 5, getHeight());
                }
            });
        }
    }

}
