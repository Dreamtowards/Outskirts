package outskirts.client.gui;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiText;
import outskirts.client.material.Texture;
import outskirts.event.EventBus;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.vector.Vector4f;

import java.util.function.Consumer;

// needs a more common-name/settingS. ?
public class GuiCheckBox extends Gui {

    private static final Texture TEX_CHECKBOX_BACKGROUND = Loader.loadTexture(new Identifier("textures/gui/checkbox/background.png").getInputStream());
    private static final Texture TEX_CHECKBOX_BACKGROUND_HOVER = Loader.loadTexture(new Identifier("textures/gui/checkbox/background_hover.png").getInputStream());
    private static final Texture TEX_CHECKBOX_ICON_CHECK = Loader.loadTexture(new Identifier("textures/gui/checkbox/check.png").getInputStream());

    private boolean checked = false;

    private GuiText text = addGui(new GuiText());


    public GuiCheckBox(String text) {
        getText().setText(text);
        getText().setRelativeXY(34, 2);

        setWrapChildren(true);

        addOnClickListener(e -> {
            toggleChecked();
        });

        GuiButton.initOnMouseDownClickSound(this);

        addOnDrawListener(e -> {
            drawTexture(isHover() ? TEX_CHECKBOX_BACKGROUND_HOVER : TEX_CHECKBOX_BACKGROUND, getX()+8, getY(), 20, 20);

            if (isHover() && Outskirts.isMouseDown(0))
                drawRect(Colors.WHITE20, getX()+8, getY(), 20, 20);

            if (isChecked())
                drawTexture(TEX_CHECKBOX_ICON_CHECK, getX()+8, getY(), 20, 20);
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
