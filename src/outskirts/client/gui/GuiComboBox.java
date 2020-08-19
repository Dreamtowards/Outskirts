package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.material.Texture;
import outskirts.util.Identifier;
import outskirts.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class GuiComboBox extends Gui {

    private static final Texture TEX_DROPDOWN_ARROW = Loader.loadTexture(new Identifier("textures/gui/combobox/chevron_new_white_right.png").getInputStream());
    private static final Texture TEX_DROPDOWN_BACKGROUND = Loader.loadTexture(new Identifier("textures/gui/combobox/dropdown_background.png").getInputStream());
    private static final Texture TEX_DROPDOWN_UNCHECKED = Loader.loadTexture(new Identifier("textures/gui/combobox/unchecked.png").getInputStream());;
    private static final Texture TEX_DROPDOWN_UNCHECKED_HOVER = Loader.loadTexture(new Identifier("textures/gui/combobox/unchecked_hover.png").getInputStream());
    private static final Texture TEX_DROPDOWN_CHECKED = Loader.loadTexture(new Identifier("textures/gui/combobox/checked.png").getInputStream());
    private static final Texture TEX_DROPDOWN_CHECKED_HOVER = Loader.loadTexture(new Identifier("textures/gui/combobox/checked_hover.png").getInputStream());

    private int selectedIndex;

    private List<GuiText> options = new ArrayList<>();

    private GuiLinearLayout dropdown = new GuiLinearLayout(Vector2f.UNIT_Y); {
        dropdown.heightRule = Gui.RULE_WRAP_CHILDREN;
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

            if (getSelectedIndex() < getOptions().size()) {
                Gui optionGui = getOptions().get(getSelectedIndex());
                float cX=optionGui.getX(), cY=optionGui.getY();
                optionGui.setX(getX()+12);
                optionGui.setY(getY()+ (getHeight()-optionGui.getHeight())/2f);
                optionGui.onDraw();
                optionGui.setX(cX); optionGui.setY(cY);
            }

            drawTexture(TEX_DROPDOWN_ARROW, getX()+getWidth() - 11 - 8, getY() + 10.5f, 11, 11);
        });

    }

    public List<GuiText> getOptions() {
        return options;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
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
        }
    }

    // update Dropdown menu.
    private void rebuildDropdown() {
        dropdown.removeAllGuis();

        dropdown.addGui(new Gui(0, 0, 0, 12)); // gap
        for (int i = 0;i < options.size();i++) {
            Gui itemGui = new Gui(0, 0, getWidth(), 24);
            // init click sound.
            GuiButton.initOnMouseDownClickSound(itemGui);
            // put the OptionGui to the item.
            itemGui.addGui(new Gui(33, 4, 0, 0)).addGui(options.get(i));
            // Dlaw
            boolean isChecked = selectedIndex==i;
            itemGui.addOnDrawListener(e -> {
                drawCornerStretchTexture(itemGui.isPressed()?TEX_DROPDOWN_CHECKED_HOVER:
                        isChecked? itemGui.isHover()?TEX_DROPDOWN_CHECKED_HOVER:TEX_DROPDOWN_CHECKED :
                                itemGui.isHover()?TEX_DROPDOWN_UNCHECKED_HOVER:TEX_DROPDOWN_UNCHECKED, itemGui.getX()+12, itemGui.getY()+5, 16, 16, 11);
            });
            // Item OnClick.
            final int fixed = i;
            itemGui.addOnClickListener(e -> {
                setSelectedIndex(fixed);
                toggleDropdown(false);
            });
            dropdown.addGui(itemGui);
        }
        dropdown.addGui(new Gui(0, 0, 0, 12)); // vertical gap
    }
}
