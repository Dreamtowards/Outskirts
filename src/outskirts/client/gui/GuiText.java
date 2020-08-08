package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.event.Cancellable;
import outskirts.event.EventBus;
import outskirts.event.EventPriority;
import outskirts.event.gui.GuiEvent;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector2i;
import outskirts.util.vector.Vector4f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class GuiText extends Gui {

    public static int DEFAULT_TEXT_HEIGHT = 16;

    private String text = "";

    private int textHeight = DEFAULT_TEXT_HEIGHT;

    private Vector4f textColor = new Vector4f(1, 1, 1, 1);

    private Vector2f textOffset = new Vector2f();

    public GuiText() {
        this("");
    }

    public GuiText(String t) {
        setText(t);
        updateTextBound(this);

        addOnDrawListener(e -> {
            drawString(text, getX() + textOffset.x, getY() + textOffset.y, textColor, textHeight);
        }).priority(EventPriority.LOW); // let text always overlay
    }

    //todo: just set(s)
    public final void setText(String text) {
        if (this.text.equals(text))
            return;
        if (performEvent(new TextChangeEvent(text)))
            return; // cancelled.

        this.text = text;

        performEvent(new TextChangedEvent());

        updateTextBound(this);
    }
    public final String getText() {
        return text;
    }

    public void setTextHeight(int textHeight) {
        this.textHeight = textHeight;
    }
    public int getTextHeight() {
        return textHeight;
    }

    public Vector4f getTextColor() {
        return textColor;
    }

    public Vector2f getTextOffset() {
        return textOffset;
    }

    public static void updateTextBound(GuiText guiText) {
        Vector2i bound = Outskirts.renderEngine.getFontRenderer().calculateBound(guiText.getText(), guiText.getTextHeight());

        guiText.setWidth(bound.x);
        guiText.setHeight(bound.y);
    }

    public static void updateTextToCenter(GuiText guiText) {
//        Vector2i bound = Outskirts.renderEngine.getFontRenderer().calculateBound(guiText.getText(), guiText.getTextHeight());
//
//        guiText.getTextOffset().set((guiText.getWidth() - bound.x) / 2, (guiText.getHeight() - bound.y) / 2);
    }

    public final EventBus.Handler addOnTextChangeListener(Consumer<TextChangeEvent> listener) {
        return attachListener(TextChangeEvent.class, listener);
    }

    public final EventBus.Handler addOnTextChangedListener(Consumer<TextChangedEvent> listener) {
        return attachListener(TextChangedEvent.class, listener);
    }

    // "Changed" listener -> e.g. in UpdateTextInfo/OffsetCenterBound
    // "Change"  listsner -> e.g. in check text content, isEmail, isNumber, and cancel.
    public static class TextChangeEvent extends GuiEvent implements Cancellable {
        private String newText;
        public TextChangeEvent(String newText) {
            this.newText = newText;
        }
        public String getNewText() {
            return newText;
        }
    }
    public static class TextChangedEvent extends GuiEvent {}


    public void addValueSyncer(Supplier<String> getter, Consumer<String> setter) {
        addOnTextChangedListener(e -> {
            setter.accept(getText());
        });
        addOnDrawListener(e -> {
            String s = getter.get();
            if (!s.equals(getText())) {
                setText(s);
            }
        });
    }
}
