package outskirts.client.gui;

import outskirts.event.Events;
import outskirts.event.client.input.KeyboardEvent;
import outskirts.util.Colors;
import outskirts.util.vector.Vector2f;

import java.util.function.Consumer;

//PopupMenu
public class GuiMenu extends Gui {

    private long lastShowMillis;

    public GuiMenu() {
        hide();

        addLayoutorLayoutLinear(Vector2f.UNIT_Y);
        addLayoutorWrapChildren(16, 4, 16, 4);

        // when mouse-click in outside, hide the menu.
        addMouseButtonListener(e -> {
            if (System.currentTimeMillis() > lastShowMillis+300 && !isMouseOver()) {  // rem topmenu
                hide();
            }
        });

        addOnDrawListener(e -> {
            drawRect(Colors.BLACK80, this);
        });
    }

    public final void show(float x, float y) {
        setX(x);
        setY(y);
        setVisible(true);
        lastShowMillis = System.currentTimeMillis();
    }

    public final void hide() {
        setVisible(false);
    }

    private static boolean isMouseInMenu(GuiMenu menu) {
        for (Gui item : menu.getChildren()) {
            if (item.isMouseOver()) {
                return true;
            }
//            else if (item.subMenu != null && item.subMenu.isVisible()) {
//                return isMouseInMenu(item.subMenu);
//            }
        }
        return false;
    }

    public static class GuiItem extends Gui {

        public static GuiItem button(String text) {
            return new GuiItem(text);
        }

        public static GuiItem bswitch(String text, boolean ck, Consumer<Boolean> onSwitch) {
            boolean[] checked = {ck};
            if (ck)onSwitch.accept(true);
            GuiItem g = new GuiItem(text);
            g.addOnClickListener(e -> {
                checked[0] = !checked[0];
                onSwitch.accept(checked[0]);
            });
            g.addOnDrawListener(e -> {
                if (checked[0]) {
                    drawString("✓", g.getX()-12, g.getY(), Colors.WHITE, GuiText.DEFAULT_TEXT_HEIGHT, false, false);
                }
            });
            return g;
        }

        public static Gui divider() {
            Gui g = new Gui();
            g.setHeight(14);
            g.addOnDrawListener(e -> drawRect(Colors.GRAY, g.getX()-16, g.getY()+6, g.getParent().getWidth(), 2));
            return g;
        }

        public static Gui slider(String s, float v, float min, float max, Consumer<Float> onChanged) {
            GuiSlider g = new GuiSlider();
            g.addValueChangedListener(e -> {
//                g.setText(String.format(s, g.getCurrentUserValue()));
                onChanged.accept(g.getCurrentUserValue());
            });
            g.setUserMinMaxValue(min, max);
            g.setCurrentUserValue(v);
            g.addLayoutorAlignParentLTRB(16, Float.NaN, 16, Float.NaN);
//            g.addOnDrawListener(e -> {
//                g.getTextOffset().set(g.isMouseOver()?g.getWidth():0, 0);
//            });
            return g;
        }

//        private static GuiItem menu(String text, GuiMenu menu) {
//            GuiItem g = new GuiItem();
//            g.setText(text);
//            g.subMenu=menu;
//            g.addGui(menu);
//            return g;
//        }
//        private GuiMenu subMenu;

        private GuiItem(String s) {
            addGui(new GuiText(s));
            setHeight(16);
            setWidth(100);
//            addOnClickListener(e -> {  // when item clicked, dismiss all sup-menu
//                Gui gui = this;
//                while ((gui=gui.getParent())!=Gui.EMPTY) {
//                    if (gui instanceof GuiMenu) ((GuiMenu)gui).hide();
//                }
//            });
//            Consumer lsr = e -> {
//                for (int i = 0;i < getParent().getChildCount();i++) {
//                    Gui item = getParent().getChildAt(i);
//                    if (item.subMenu != null) {
//                        if (item.isMouseOver()) {
//                            item.subMenu.setX(getX() + getWidth()).setY(getY());
//                            item.subMenu.setVisible(true);
//                        } else if (item.subMenu.isVisible() && !isMouseInMenu(item.subMenu)) {
//                            item.subMenu.hide();
//                        }
//                    }
//                }
//            };
//            addOnMouseExitedListener(lsr);
//            addOnMouseEnteredListener(lsr);

            addOnDrawListener(e -> {
                Gui paren = getParent();
                if (isMouseOver(paren.getX(), getY(), paren.getWidth(), getHeight())) {
                    drawRect(Colors.BLACK40, paren.getX(), getY(), paren.getWidth(), getHeight());
//                    drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());
                }
//                if (subMenu != null) {
//                    drawRect(Colors.GREEN, paren.getX() + paren.getWidth() - 5, getY(), 5, getHeight());
//                }
            });
        }

        public GuiItem bindKey(int key) {
            Events.EVENT_BUS.register(KeyboardEvent.class, e -> {
                if (e.getKeyState() && e.getKey() == key) {
                    performEvent(new OnClickEvent());
                }
            });
            return this;
        }
    }

}
