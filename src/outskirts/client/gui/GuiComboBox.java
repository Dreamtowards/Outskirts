package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.render.Texture;
import outskirts.event.EventBus;
import outskirts.event.EventPriority;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiComboBox extends Gui {

    private static final Texture TEX_DROPDOWN_ARROW = Loader.loadTexture(new Identifier("textures/gui/combobox/chevron_new_white_right.png").getInputStream());
    private static final Texture TEX_DROPDOWN_BACKGROUND = Loader.loadTexture(new Identifier("textures/gui/combobox/dropdown_background.png").getInputStream());
    private static final Texture TEX_DROPDOWN_UNCHECKED = Loader.loadTexture(new Identifier("textures/gui/combobox/unchecked.png").getInputStream());;
    private static final Texture TEX_DROPDOWN_UNCHECKED_HOVER = Loader.loadTexture(new Identifier("textures/gui/combobox/unchecked_hover.png").getInputStream());
    private static final Texture TEX_DROPDOWN_CHECKED = Loader.loadTexture(new Identifier("textures/gui/combobox/checked.png").getInputStream());
    private static final Texture TEX_DROPDOWN_CHECKED_HOVER = Loader.loadTexture(new Identifier("textures/gui/combobox/checked_hover.png").getInputStream());

    private int selectedIndex = -1;

    private List<Gui> options = new ArrayList<>();

    private GuiColumn dropdown = new GuiColumn(); {
        dropdown.setHeight(NaN);
        dropdown.addOnDrawListener(e -> {
            drawCornerStretchTexture(TEX_DROPDOWN_BACKGROUND, dropdown, 5);
        });
        dropdown.addOnLayoutListener(e -> {
            dropdown.setX(getX());
            dropdown.setY(getY());
            dropdown.setWidth(getWidth());
        });
        dropdown.addOnFocusChangedListener(e -> {
            if (!dropdown.isFocused()) {
                toggleDropdown(false);
            }
        });
    }

    public GuiComboBox() {
        setWidth(180);
        setHeight(32);

        GuiButton.initOnMouseDownClickSound(this);

        addOnPressedListener(e -> {
            toggleDropdown(true);
        });

        addOnDrawListener(e -> {

            GuiButton.drawButtonBackground(this);

            // dlaw selected.
            if (getSelectedIndex() >= 0 && getSelectedIndex() < getOptions().size()) {
                boolean tmpDisableLayoutReq = Gui.layoutRequested;
                Gui optionGui = getOptions().get(getSelectedIndex());
                float cX=optionGui.getX(), cY=optionGui.getY();
                optionGui.setX(getX()+12);
                optionGui.setY(getY()+ (getHeight()-optionGui.getHeight())/2f);
                optionGui.onDraw();
                optionGui.setX(cX); optionGui.setY(cY);
                Gui.layoutRequested = tmpDisableLayoutReq;
            }

            drawTexture(TEX_DROPDOWN_ARROW, getX()+getWidth() - 11 - 8, getY()+(getHeight()-11)/2f, 11, 11);
        });

    }

    public List<Gui> getOptions() {
        return options;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
    public void setSelectedIndex(int selectedIndex) {
        int oldsel = this.selectedIndex;
        this.selectedIndex = selectedIndex;
        if (oldsel != selectedIndex) {
            performEvent(new OnSelectedEvent());
        }
    }

    public final EventBus.Handler addOnSelectedListener(Consumer<OnSelectedEvent> lsr) {
        return attachListener(OnSelectedEvent.class, lsr);
    }

    public void toggleDropdown(boolean open) {
        if (open) {
            // todo: is this a right "Tooltip" displaying way.?
            Gui.getRootGUI().addGui(dropdown);
            dropdown.setHover(true); // for Focused
            dropdown.setFocused(true); // unnesecary
            rebuildDropdown();
        } else {
            Gui.getRootGUI().removeGui(dropdown);
            for (Gui g : options) {
                g.getParent().removeGui(g);
            }
        }
    }

    // update Dropdown menu.
    private void rebuildDropdown() {
        dropdown.removeAllGuis();
        dropdown
                .addChildren(new Gui(0, 0, 0, 12)) // vertical gap.
                .exec(g -> {
                    for (int i = 0;i < options.size();i++) {
                        final int fixedI = i;
                        g.addChildren(
                          new Gui(0,0,getWidth(),24).exec(gitem -> {
                              GuiButton.initOnMouseDownClickSound(gitem); // init click sound.
                              // Dlaw
                              boolean isChecked = selectedIndex==fixedI;
                              gitem.addOnDrawListener(e -> {
                                  drawCornerStretchTexture(gitem.isPressed()?TEX_DROPDOWN_CHECKED_HOVER:
                                          isChecked? gitem.isHover()?TEX_DROPDOWN_CHECKED_HOVER:TEX_DROPDOWN_CHECKED :
                                                  gitem.isHover()?TEX_DROPDOWN_UNCHECKED_HOVER:TEX_DROPDOWN_UNCHECKED, gitem.getX()+12, gitem.getY()+5, 16, 16, 11);
                              });
                              // Item OnClick.
                              gitem.addOnClickListener(e -> {
                                  setSelectedIndex(fixedI);
                                  toggleDropdown(false);
                              });
                          }).addChildren(
                            new Gui(33, 4, 0, 0).addChildren(options.get(i))  // put the OptionGui to the item.
                          )
                        );
                    }
                })
                .addChildren(new Gui(0, 0, 0, 12));
    }

    public static class OnSelectedEvent extends GuiEvent { }


    public final void initSelectionSync(Supplier<Integer> get, Consumer<Integer> set) {
        addOnSelectedListener(e -> {
            int thisi = getSelectedIndex();
            if (get.get() != thisi) {
                set.accept(thisi);
            }
        });
        addOnDrawListener(e -> {
            int i = get.get();
            if (i != getSelectedIndex()) {
                setSelectedIndex(i);
            }
        }).priority(EventPriority.HIGH);
    }
}
