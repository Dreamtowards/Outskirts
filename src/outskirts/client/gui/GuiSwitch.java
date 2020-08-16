package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.material.Texture;
import outskirts.util.Identifier;

public class GuiSwitch extends Gui implements Gui.Checkable {

    private static final Texture TEX_SWITCH_OFF = Loader.loadTexture(new Identifier("textures/gui/switch/toggle_off.png").getInputStream());
    private static final Texture TEX_SWITCH_OFF_HOVER = Loader.loadTexture(new Identifier("textures/gui/switch/toggle_off_hover.png").getInputStream());
    private static final Texture TEX_SWITCH_ON = Loader.loadTexture(new Identifier("textures/gui/switch/toggle_on.png").getInputStream());
    private static final Texture TEX_SWITCH_ON_HOVER = Loader.loadTexture(new Identifier("textures/gui/switch/toggle_on_hover.png").getInputStream());

    private boolean checked;

    public GuiSwitch() {
        setWidth(52);
        setHeight(28);

        GuiButton.initOnMouseDownClickSound(this);

        addOnPressedListener(e -> {
            setChecked(!isChecked());
        });

        addOnDrawListener(e -> {
            drawTexture(
                    isChecked()? isHover()?TEX_SWITCH_ON_HOVER:TEX_SWITCH_ON :
                                 isHover()?TEX_SWITCH_OFF_HOVER:TEX_SWITCH_OFF, this);
        });
    }

    @Override
    public boolean isChecked() {
        return checked;
    }
    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
