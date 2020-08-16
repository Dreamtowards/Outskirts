package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.material.Texture;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

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

    private Gui dropdown = new Gui(); {
        dropdown.addOnDrawListener(e -> {
            drawCornerStretchTexture(TEX_DROPDOWN_BACKGROUND, dropdown, 5);
        });
        dropdown.addOnLayoutListener(e -> {
            dropdown.setWidth(getWidth());
            dropdown.setHeight(dropdown.getGui(0).getHeight());
            dropdown.setX(getX());
            dropdown.setY(getY());
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
                Gui option = getOptions().get(getSelectedIndex());
                float cX=option.getX(), cY=option.getY();
                option.setX(getX()+12);
                option.setY(getY()+8);
                option.onDraw();
                option.setX(cX); option.setY(cY);
            }

            drawTexture(TEX_DROPDOWN_ARROW, getX()+getWidth() - 11 - 8, getY() + 10.5f, 11, 11);
        });

    }

    public List<GuiText> getOptions() {
        return options;
    }

    public void toggleDropdown(boolean open) {

        if (open) {
            Gui.getRootGUI().addGui(dropdown);
            dropdown.setFocused(true);
            dropdown.setHover(true); // for Focused

            // update Dropdown menu.
            dropdown.removeAllGuis();
            GuiLinearLayout dropdownItems = dropdown.addGui(new GuiPadding(new Insets(8, 12, 8, 12)))
                    .addGui(new GuiLinearLayout(Vector2f.UNIT_Y));
            dropdownItems.setWrapChildren(true);
            for (int i = 0;i < options.size();i++) {
                GuiText optionItem = options.get(i);
                boolean checked = selectedIndex==i;
                Gui itemGui = new Gui(0, 0, getWidth(), 24); // todo: padding there actually shouldn't, padding is secondary property, not mainly.
                optionItem.setRelativeXY(25, 4);
                itemGui.addGui(optionItem);
                itemGui.addOnDrawListener(e -> {
                    drawCornerStretchTexture(itemGui.isPressed()?TEX_DROPDOWN_CHECKED_HOVER:
                            checked? itemGui.isHover()?TEX_DROPDOWN_CHECKED_HOVER:TEX_DROPDOWN_CHECKED :
                                     itemGui.isHover()?TEX_DROPDOWN_UNCHECKED_HOVER:TEX_DROPDOWN_UNCHECKED, itemGui.getX()+4, itemGui.getY()+5, 16, 16, 11);
                });
                GuiButton.initOnMouseDownClickSound(itemGui);
                final int itemIndex = i;
                itemGui.addOnClickListener(e -> {
                    setSelectedIndex(itemIndex);
                    toggleDropdown(false);
                });
                dropdownItems.addGui(itemGui);
            }
        } else {
            Gui.getRootGUI().removeGui(dropdown);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
