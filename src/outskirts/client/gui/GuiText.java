package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.event.Cancellable;
import outskirts.event.EventPriority;
import outskirts.event.gui.GuiEvent;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector2i;
import outskirts.util.vector.Vector4f;

import java.util.function.Consumer;

public class GuiText extends Gui {

    public static int DEFAULT_TEXT_HEIGHT = 16;

    private String text = "";

    private int textHeight = DEFAULT_TEXT_HEIGHT;

    private Vector4f textColor = new Vector4f(1, 1, 1, 1);

    private Vector2f textOffset = new Vector2f();

    public GuiText() {
        // addBeforeDrawListener
        addOnDrawListener(e -> {

            drawString(text, getX() + textOffset.x, getY() + textOffset.y, textColor, textHeight);
        }, EventPriority.LOW); // let text always overlay
    }

    public final <T extends GuiText> T setText(String text) {
        if (this.text.equals(text))
            return (T)this;
        if (performEvent(new TextChangeEvent(text)))
            return (T)this; // cancelled.
        this.text = text;
        performEvent(new TextChangedEvent());
        return (T)this;
    }
    public final String getText() {
        return text;
    }

    public <T extends GuiText> T setTextHeight(int textHeight) {
        this.textHeight = textHeight;
        return (T)this;
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

        guiText.setWidth(bound.x).setHeight(bound.y);
    }

    public static void updateTextToCenter(GuiText guiText) {
        Vector2i bound = Outskirts.renderEngine.getFontRenderer().calculateBound(guiText.getText(), guiText.getTextHeight());

        guiText.getTextOffset().set((guiText.getWidth() - bound.x) / 2, (guiText.getHeight() - bound.y) / 2);
    }

    public final <T extends GuiText> T addOnTextChangeListener(Consumer<TextChangeEvent> listener) {
        attachListener(TextChangeEvent.class, listener);return (T)this;
    }

    public final <T extends GuiText> T addOnTextChangedListener(Consumer<TextChangedEvent> listener) {
        attachListener(TextChangedEvent.class, listener);return (T)this;
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
}
