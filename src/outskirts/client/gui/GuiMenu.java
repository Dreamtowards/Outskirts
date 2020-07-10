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
            if (lastShowMillis+300 < System.currentTimeMillis() && !isMouseOver()) {  // rem topmenu
                hide();
            }
        });

        addOnDrawListener(e -> {
            drawRect(Colors.BLACK80, this);
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

    public static class GuiItem extends GuiText {

        public static GuiItem button(String text) {
            return new GuiItem(text);
        }

        public static GuiItem bswitch(String text, boolean ck, Consumer<Boolean> onSwitch) {
            boolean[] checked = {ck};
            if (ck)onSwitch.accept(true);
            return new GuiItem(text).addOnClickListener(e -> {
                checked[0] = !checked[0];
                onSwitch.accept(checked[0]);
            }).addOnDrawListener(e -> {
                if (checked[0]) {
                    Gui g = e.gui();
                    drawString("✓", g.getX()-12, g.getY(), Colors.WHITE, GuiText.DEFAULT_TEXT_HEIGHT, false, false);
                }
            });
        }

        public static Gui divider() {
            return new Gui()
                    .setHeight(14)
                    .addOnDrawListener(e -> {
                        Gui g = e.gui();
                        drawRect(Colors.GRAY, g.getX()-16, g.getY()+6, g.getParent().getWidth(), 2);
                    });
        }

        public static Gui slider(String s, float v, float min, float max, Consumer<Float> onChanged) {
            return new GuiSlider()
                    .addValueChangedListener(e -> {
                        GuiSlider g = e.gui();
                        g.setText(String.format(s, g.getCurrentUserValue()));
                        onChanged.accept(g.getCurrentUserValue());
                    })
                    .setUserMinMaxValue(min, max)
                    .setCurrentUserValue(v)
                    .addLayoutorAlignParentLTRB(16, Float.NaN, 16, Float.NaN)
                    .addOnDrawListener(e -> {
                        GuiSlider g = e.gui();
                        g.getTextOffset().set(g.isMouseOver()?g.getWidth():0, 0);
                    });
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
            super(s);
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
