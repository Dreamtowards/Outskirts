package outskirts.client.gui;

import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiText;
import outskirts.event.EventBus;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.vector.Vector4f;

import java.util.function.Consumer;

// needs a more common-name/settingS. ?
public class GuiCheckBox extends Gui {

    private boolean checked = false;

    private GuiText text = addGui(new GuiText());


    public GuiCheckBox(String text) {
        getText().setText(text);
        getText().setRelativeXY(32, 8);

        setWidth(150);
        setHeight(32);

        addOnClickListener(e -> {
            toggleChecked();
            GuiButton.playClickSound();
        });
        addOnDrawListener(e -> {
            int OUTR_SIZE = 16;
            int OUTR_THIN = 2;
            float iconX = 8 +getX();
            float iconY = (getHeight()-OUTR_SIZE)/2 +getY();
            drawRectBorder(Colors.GRAY, iconX, iconY, OUTR_SIZE, OUTR_SIZE, OUTR_THIN);

            int INNR_BORDER = 2;
            int INNR_SIZE = OUTR_SIZE-2*OUTR_THIN-2*INNR_BORDER;
            if (isChecked())
                GuiButton.drawButtonTexture(GuiButton.TEXTURE_BUTTON_NORMAL, iconX+OUTR_THIN+INNR_BORDER, iconY+OUTR_THIN+INNR_BORDER, INNR_SIZE, INNR_SIZE);
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

    public final EventBus.Handler addOnCheckedChangedListener(Consumer<CheckedEvent> lsr) {
        return attachListener(CheckedEvent.class, lsr);
    }

    public static class CheckedEvent extends GuiEvent { }
}
