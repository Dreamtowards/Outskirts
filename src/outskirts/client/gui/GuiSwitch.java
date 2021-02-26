package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.render.Texture;
import outskirts.event.EventPriority;
import outskirts.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
        }).priority(EventPriority.HIGH);

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
        if (this.checked != checked) {
            this.checked = checked;
            performEvent(new OnCheckedEvent());
        }
    }

}
