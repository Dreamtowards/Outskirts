package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.material.Texture;
import outskirts.util.Colors;
import outskirts.util.Identifier;

// todo: Checkable .?
public class GuiRadioButton extends Gui {

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
        setWidth(30);
        setHeight(20);
        setWrapChildren(true);
        getText().setText(s);
        getText().setRelativeXY(24, 2);

        GuiButton.initOnMouseDownClickSound(this);

        addOnClickListener(e -> {
            setChecked(true);
            for (Gui g : getParent().getChildren()) {
                if (g != this && g instanceof GuiRadioButton) {
                    ((GuiRadioButton)g).setChecked(false);
                }
            }
        });

        addOnDrawListener(e -> {
            drawTexture(isChecked()? isHover()?TEX_RADIO_ON_HOVER:TEX_RADIO_ON :
                                     isHover()?TEX_RADIO_OFF_HOVER:TEX_RADIO_OFF, getX(), getY(), 20, 20);
            if (isPressed()) {
                drawRect(Colors.BLACK05, getX(), getY(), 20, 20);
            }
        });
    }

    public GuiText getText() {
        return text;
    }

    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
