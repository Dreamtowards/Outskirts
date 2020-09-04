package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.material.Texture;
import outskirts.util.Colors;
import outskirts.util.Identifier;

import java.util.function.Consumer;

public class GuiPopupMenu extends Gui {

    private static final Texture TEX_SUBMENU_ARROW = Loader.loadTexture(new Identifier("textures/gui/popupmenu/arrow.png").getInputStream());

    private long lastShowMillis;

    private GuiColumn itemlist;

    public GuiPopupMenu() {
        addChildren(
          itemlist=new GuiColumn()
        );

        hide();

        // when mouse-click in outside, hide the menu.
        addMouseButtonListener(e -> {
            if (System.currentTimeMillis() > lastShowMillis+300 && !isMenuHover()) {  // rem topmenu
                hide();
            }
        });

        addOnDrawListener(e -> {
            drawRect(Colors.BLACK, this);
        });
    }

    public final void show(float x, float y) {
        setX(x); setY(y);
        lastShowMillis = System.currentTimeMillis();
        if (isVisible())return;
        setVisible(true);
        Gui.getRootGUI().addGui(this);
//        Log.LOGGER.info("Show Menu");
    }

    public final void hide() {
        if (!isVisible())return;
        setVisible(false);
        Gui.getRootGUI().removeGui(this);
        // recursive hide sub menus..
        for (Gui g : itemlist.getChildren()) {
            if (g instanceof GuiItem && ((GuiItem)g).submenu != null)
                ((GuiItem)g).submenu.hide();
        }

//        Log.LOGGER.info("Hide Menu");
    }

    // indicates sub-item menus...
    private boolean isMenuHover() {
        if (isHover())
            return true;
        for (Gui g : itemlist.getChildren()) {
            if (g instanceof GuiItem) {
                GuiPopupMenu submenu = ((GuiItem)g).submenu;
                if (submenu != null && submenu.isMenuHover())
                    return true;
            }
        }
        return false;
    }

    public void addItem(Gui guiItem) {

        itemlist.addGui(guiItem);

    }

    public static class GuiItem extends Gui {

        public static GuiItem button(String text, Runnable onClick) {
            GuiItem g = new GuiItem(new GuiText(text));
            g.addOnClickListener(e -> onClick.run());
//            g.addOnClickListener(e -> {  // when item clicked, dismiss all sup-popupmenus
//                Gui.forParents(g, pa -> {
//                    if (pa instanceof GuiPopupMenu)
//                        ((GuiPopupMenu)pa).hide();
//                }, false);
//            });
            return g;
        }

        public static GuiItem bswitch(String text, boolean ck, Consumer<Boolean> onSwitch) {
            GuiCheckBox sw = new GuiCheckBox(text);
            sw.setChecked(ck);
            sw.addOnCheckedListener(e -> {
                onSwitch.accept(sw.isChecked());
            });
            return new GuiItem(sw);
        }

        public static Gui divider() {
            Gui g = new Gui();
            g.setHeight(6);
            g.addOnDrawListener(e -> drawRect(Colors.WHITE, g.getX(), g.getY()+1, g.getParent().getWidth(), 4));
            return g;
        }

        public static Gui slider(String s, float v, float min, float max, Consumer<Float> onChanged) {
            GuiSlider g = new GuiSlider();
            g.addOnValueChangedListener(e -> {
                onChanged.accept(g.getCurrentUserValue());
            });
            g.setUserMinMaxValue(min, max);
            g.setCurrentUserValue(v);
            return new GuiItem(g);
        }

        public static GuiItem menu(String text, GuiPopupMenu menu) {
            GuiItem g = new GuiItem(new GuiText(text));
            g.submenu =menu;
            return g;
        }
        private GuiPopupMenu submenu;

        private GuiItem(Gui content) {
            addGui(content);
            content.addLayoutorAlignParentRR(Float.NaN, 0.45f);
            content.addLayoutorAlignParentLTRB(8, Float.NaN, 8, Float.NaN);
            setHeight(38);
            setWidth(190);
//            Consumer lsr = e -> {
//                Gui.forChildren(getParent(), g -> {
//                    GuiItem item = (GuiItem)g;
//                    if (item.subMenu != null) {
//                        if (item.isHover()) {
//                            item.subMenu.setX(getX() + getWidth()).setY(getY());
//                            item.subMenu.setVisible(true);
//                        } else if (item.subMenu.isVisible() && !isMouseInMenu(item.subMenu)) {
//                            item.subMenu.hide();
//                        }
//                    }
//                }, false);
//            };
            Consumer lsrOpenMenu = e -> {
                Gui.forChildren(getParent(), g -> {
                    if (g instanceof GuiItem && ((GuiItem)g).submenu != null)
                        ((GuiItem)g).submenu.hide();
                }, false);
                if (submenu != null) {
                    submenu.show(getX()+getWidth(), getY());
                }
            };
            addOnMouseInListener(lsrOpenMenu);
            addOnPressedListener(lsrOpenMenu);
            addOnClickListener(lsrOpenMenu);
            addOnMouseOutListener(e -> {
                if (submenu != null && !submenu.isMenuHover()) {
                    submenu.hide();
                }
            });

            addOnDrawListener(e -> {
                drawCornerStretchTexture(GuiButton.TEX_BUTTON_BACKGROUND, this, 3);
                if (isHover()) {
                    drawRect(Colors.BLACK10, this);
                }
                if (submenu != null) {
                    drawTexture(TEX_SUBMENU_ARROW, getX()+getWidth() - 20, getY()+(getHeight()-14)/2, 14, 14);
                }
            });
        }
    }

}
