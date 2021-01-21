package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.render.Texture;
import outskirts.event.EventBus;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Identifier;

import java.util.function.Consumer;

// needs a more common-name/settingS. ?
public class GuiCheckBox extends Gui implements Gui.Checkable {

    private static final Texture TEX_CHECKBOX_BACKGROUND = Loader.loadTexture(new Identifier("textures/gui/checkbox/background.png").getInputStream());
    private static final Texture TEX_CHECKBOX_BACKGROUND_HOVER = Loader.loadTexture(new Identifier("textures/gui/checkbox/background_hover.png").getInputStream());
    private static final Texture TEX_CHECKBOX_BACKGROUND_PRESSED = Loader.loadTexture(new Identifier("textures/gui/checkbox/background_pressed.png").getInputStream());
    private static final Texture TEX_CHECKBOX_ICON_CHECK = Loader.loadTexture(new Identifier("textures/gui/checkbox/check.png").getInputStream());

    private boolean checked = false;

    private GuiText text = addGui(new GuiText());


    public GuiCheckBox(String text) {
        setHeight(20);
        getText().setText(text);
        getText().setRelativeXY(26, 2);

        addOnClickListener(e -> {
            toggleChecked();
        });

        GuiButton.initOnMouseDownClickSound(this);

        addOnDrawListener(e -> {
            drawTexture(isPressed() ? TEX_CHECKBOX_BACKGROUND_PRESSED : isHover() ? TEX_CHECKBOX_BACKGROUND_HOVER : TEX_CHECKBOX_BACKGROUND, getX(), getY(), 20, 20);

            if (isChecked())
                drawTexture(TEX_CHECKBOX_ICON_CHECK, getX(), getY(), 20, 20);
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
        performEvent(new CheckedEvent());
    }

    private void toggleChecked() {
        setChecked(!isChecked());
    }

    public final EventBus.Handler addOnCheckedListener(Consumer<CheckedEvent> lsr) {
        return attachListener(CheckedEvent.class, lsr);
    }

    public static class CheckedEvent extends GuiEvent { }
}
