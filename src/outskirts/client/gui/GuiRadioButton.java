package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.render.Texture;
import outskirts.util.Colors;
import outskirts.util.Identifier;

public class GuiRadioButton extends Gui implements Gui.Checkable {

    private static final Texture TEX_RADIO_OFF = Loader.loadTexture(new Identifier("textures/gui/radio_button/radio_off.png").getInputStream());
    private static final Texture TEX_RADIO_OFF_HOVER = Loader.loadTexture(new Identifier("textures/gui/radio_button/radio_off_hover.png").getInputStream());
    private static final Texture TEX_RADIO_ON = Loader.loadTexture(new Identifier("textures/gui/radio_button/radio_on.png").getInputStream());
    private static final Texture TEX_RADIO_ON_HOVER = Loader.loadTexture(new Identifier("textures/gui/radio_button/radio_on_hover.png").getInputStream());
    private static final Texture TEX_RADIO_ON_PRESSED = Loader.loadTexture(new Identifier("textures/gui/radio_button/radio_on_pressed.png").getInputStream());

    private boolean checked;

    private GuiText text = addGui(new GuiText());


    public GuiRadioButton() {
        this("");
    }

    public GuiRadioButton(String s) {
        setHeight(20);
        getText().setText(s);
        getText().setRelativeXY(24, 2);

        GuiButton.initOnMouseDownClickSound(this);

        addOnClickListener(e -> {
            setChecked(true);
        });

        addOnDrawListener(e -> {
            drawTexture(isChecked()? isHover()?TEX_RADIO_ON_HOVER:TEX_RADIO_ON :
                                     isHover()?TEX_RADIO_OFF_HOVER:TEX_RADIO_OFF, getX(), getY(), 20, 20);
            if (isPressed()) {
                drawRect(Colors.WHITE10, getX(), getY(), 20, 20);
            }
        });
    }

    public GuiText getText() {
        return text;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }
    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checked) {
            for (Gui sibling : getParent().getChildren()) {
                if (sibling != this && sibling instanceof GuiRadioButton) {
                    ((GuiRadioButton)sibling).setChecked(false);
                }
            }
        }
    }
}
