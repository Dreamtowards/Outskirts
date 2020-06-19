package outskirts.client.gui;

import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiText;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.vector.Vector4f;

import java.util.function.Consumer;

// needs a more common-name/settingS. ?
public class GuiCheckBox extends GuiText {

    private boolean checked = false;

    public GuiCheckBox(String text) {
        this();setText(text);
    }

    public GuiCheckBox() {
        setWidth(250);
        setHeight(40);

        getTextOffset().set(46, 12);

        addOnClickListener(e -> {
            if (isMouseOver()) {
                toggleChecked();
                GuiButton.playClickSound();
            }
        });
        addOnDrawListener(e -> {
            drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());

            int OUTR_SIZE = 24;
            int OUTR_THIN = 3;
            float iconX = 14 +getX();
            float iconY = (getHeight()-OUTR_SIZE)/2 +getY();

            drawRectBorder(Colors.GRAY, iconX, iconY, OUTR_SIZE, OUTR_SIZE, OUTR_THIN);

            int INNR_BORDER = 2;
            int INNR_SIZE = OUTR_SIZE-2*OUTR_THIN-2*INNR_BORDER;
            drawRect(isChecked()?Colors.GREEN:isMouseOver()?Colors.WHITE05: Vector4f.ZERO, iconX+OUTR_THIN+INNR_BORDER, iconY+OUTR_THIN+INNR_BORDER, INNR_SIZE, INNR_SIZE);
        });
    }

    public boolean isChecked() {
        return checked;
    }
    public <T extends GuiCheckBox> T setChecked(boolean checked) {
        this.checked = checked;
        performEvent(new OnCheckedChangedEvent());
        return (T)this;
    }

    private void toggleChecked() {
        setChecked(!isChecked());
    }

    public final <T extends GuiCheckBox> T addOnCheckedChangedListener(Consumer<OnCheckedChangedEvent> lsr) {
        attachListener(OnCheckedChangedEvent.class, lsr); return (T)this;
    }

    public static class OnCheckedChangedEvent extends GuiEvent { }
}
