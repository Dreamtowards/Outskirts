package outskirts.client.gui;

import outskirts.util.Colors;

import java.util.function.Consumer;

public class GuiMenu extends Gui {

    private Gui itemsGui = addGui(new GuiLayoutLinear().setOrientation(GuiLayoutLinear.VERTICAL));
    private boolean standalone = true;

    public GuiMenu() {
        hide();
        
        addMouseButtonListener(e -> {
            if (standalone && e.getButtonState()) {
                if (!isMouseInMenu(this)) {
                    hide();
                }
            }
        });
    }

    public Gui getItemsGui() {
        return itemsGui;
    }

    public final void show(float x, float y) {
        setX(x);
        setY(y);
        setVisible(true);
    }

    public final void hide() {
        setVisible(false);
        forChildren(child -> {
            if (child instanceof GuiMenu) {
                child.setVisible(false);
            }
        }, true);
    }

    private static boolean isMouseInMenu(GuiMenu menu) {
        for (int i = 0;i < menu.itemsGui.getChildCount();i++) {
            GuiMenuItem item = menu.itemsGui.getChildAt(i);
            if (item.isMouseOver()) {
                return true;
            } else if (item.subMenu != null && item.subMenu.isVisible()) {
                return isMouseInMenu(item.subMenu);
            }
        }
        return false;
    }

    public static class GuiMenuItem extends GuiText {

        private GuiMenu subMenu;

        public GuiMenuItem(String text) {
            setText(text);
            updateTextBound(this);
            Consumer lsr = e -> {
                for (int i = 0;i < getParent().getChildCount();i++) {
                    GuiMenuItem item = getParent().getChildAt(i);
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
        }

        public GuiMenuItem() {
            this("");

            addOnDrawListener(e -> {
                if (isMouseOver()) {
                    drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());
                }
                drawRect(Colors.WHITE20, getX(), getY(), getWidth(), getHeight());
                if (subMenu != null) {
                    drawRect(Colors.GREEN, getX() + getWidth() - 5, getY(), 5, getHeight());
                }
            });
        }

        public void setSubMenu(GuiMenu subMenu) {
            this.subMenu = subMenu;
            subMenu.standalone = false;
            addGui(subMenu);
        }
    }

}
