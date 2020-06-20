package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.client.render.renderer.FontRenderer;
import outskirts.event.Cancellable;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2i;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;

public class GuiTextField extends GuiText {

    private int cursorPosition;

    private int selectionBegin;
    private int selectionEnd;

    private long lastFocusedTime;

    private Gui cursorGui = addGui(new Gui()).addOnDrawListener(e -> {
        if (isFocused() && ((System.currentTimeMillis() / 500) % 2 == 0 || lastFocusedTime > System.currentTimeMillis() - 600)) {
            drawRect(Colors.WHITE, e.gui().getX(), e.gui().getY(), e.gui().getWidth(), getTextHeight());
        }
    }).setWidth(3);

    {
        setWidth(120);
        setHeight(16);

        addMouseButtonListener(e -> {
            if (e.getMouseButton() == GLFW_MOUSE_BUTTON_LEFT && e.getButtonState() && isMouseOver()) {
                int cursorPos = calculateCurrentCursorPosition();
                if (cursorPos != -1) {
                    setCursorPosition(cursorPos);
                    setSelectionBegin(cursorPos);
                    setSelectionEnd(cursorPos);
                }
            }
        });

        addMouseMoveListener(e -> {
            if (Outskirts.isMouseDown(GLFW_MOUSE_BUTTON_LEFT) && isMouseOver()) {  // || (e.getMouseButton() == 0 && !e.getButtonState())
                int cursorPos = calculateCurrentCursorPosition();
                if (cursorPos != -1) {
                    setCursorPosition(cursorPos);
                    setSelectionEnd(getCursorPosition());
                }
            }
        });

        addKeyboardListener(e -> {
            if (isFocused() && e.getKeyState()) {
                int keyCode = e.getKey();
                if (keyCode == GLFW_KEY_ENTER) {
                    insertText("\n");
                } else if (keyCode == GLFW_KEY_BACKSPACE) {
                    if (getCursorPosition() > 0) {
                        if (!isSelectedText()) {
                            setSelectionBegin(getCursorPosition() - 1);
                            setSelectionEnd(getCursorPosition());
                        }
                        insertText("");
                    }
                } else if (keyCode == GLFW_KEY_LEFT) {
                    if (isSelectedText())
                        setCursorPosition(getMinSelection());
                    else
                        setCursorPosition(getCursorPosition() - 1);
                    setSelectionEmpty();
                } else if (keyCode == GLFW_KEY_RIGHT) {
                    if (isSelectedText())
                        setCursorPosition(getMaxSelection());
                    else
                        setCursorPosition(getCursorPosition() + 1);
                    setSelectionEmpty();
                } else if (Outskirts.isCtrlKeyDown()) {
                    if (keyCode == GLFW_KEY_A) {
                        setSelectionBegin(0);
                        setSelectionEnd(getText().length());
                    } else if (keyCode == GLFW_KEY_C) {
                        if (isSelectedText()) {
                            Outskirts.setClipboard(getSelectedText());
                        }
                    } else if (keyCode == GLFW_KEY_V) {
                        insertText(Outskirts.getClipboard());
                    } else if (keyCode == GLFW_KEY_X) {
                        if (isSelectedText()) {
                            Outskirts.setClipboard(getSelectedText());
                            insertText("");
                        }
                    }
                }
            }
        });

        addCharInputListener(e -> {
            if (isFocused()) {
                insertText(Character.toString(e.getChar()));
            }
        });

        addOnDrawListener(e -> {
            drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());

            if (getCursorPosition() > getText().length())
                setCursorPosition(getCursorPosition()); // clamp/checks cursor position in texts. some times cursorposition had been customed, but then text been setted to empty...

            // set cursor display position
            Vector2i cursorPos = Outskirts.renderEngine.getFontRenderer().calculateTextPosition(getText(), getTextHeight(), getCursorPosition(), null);
            cursorGui.setX(getX() + getTextOffset().x + cursorPos.x)
                    .setY(getY() + getTextOffset().y + cursorPos.y);

            // draw selection
            Vector2i TMP_CACHE = new Vector2i();
            for (int i = getMinSelection();i < getMaxSelection();i++) {
                Vector2i pos = Outskirts.renderEngine.getFontRenderer().calculateTextPosition(getText(), getTextHeight(), i, TMP_CACHE);
                int charWidth = (int)(Outskirts.renderEngine.getFontRenderer().charWidth(getText().charAt(i)) * getTextHeight());
                drawRect(Colors.WHITE20, getX() + getTextOffset().x + pos.x, getY() + getTextOffset().y + pos.y, charWidth + FontRenderer.GAP_CHAR, getTextHeight());
            }
        });
    }

    public GuiTextField() {}

    public GuiTextField(String s) {
        setText(s);
    }

    private int calculateCurrentCursorPosition() {
        return Outskirts.renderEngine.getFontRenderer().calculateTextIndex(getText(), getTextHeight(),
                Outskirts.getMouseX() - getX() - getTextOffset().x,
                Outskirts.getMouseY() - getY() - getTextOffset().y);
    }

    public void insertText(String text) {
        if (performEvent(new TextInsertedEvent(text)))
            return;
        if (isSelectedText()) {
            setText(getText().substring(0, getMinSelection()) + text + getText().substring(getMaxSelection()));
            setCursorPosition(getMinSelection() + text.length());

            setSelectionEmpty();
        } else {
            setText(getText().substring(0, getCursorPosition()) + text + getText().substring(getCursorPosition()));
            setCursorPosition(getCursorPosition() + text.length());
        }
    }

    @Override
    public <T extends Gui> T setFocused(boolean focused) {
        lastFocusedTime = System.currentTimeMillis();
        return super.setFocused(focused);
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int cursorPosition) {
        setFocused(true);
        this.cursorPosition = Maths.clamp(cursorPosition, 0, getText().length());
    }

    public int getSelectionBegin() {
        return selectionBegin;
    }
    public void setSelectionBegin(int selectionBegin) {
        this.selectionBegin = selectionBegin;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }
    public void setSelectionEnd(int selectionEnd) {
        this.selectionEnd = selectionEnd;
    }

    private void setSelectionEmpty() {
        setSelectionBegin(0);
        setSelectionEnd(0);
    }

    public int getMinSelection() {
        return Math.min(getSelectionBegin(), getSelectionEnd());
    }
    public int getMaxSelection() {
        return Math.max(getSelectionBegin(), getSelectionEnd());
    }
    public boolean isSelectedText() {
        return getSelectionBegin() != getSelectionEnd();
    }
    public String getSelectedText() {
        return getText().substring(getMinSelection(), getMaxSelection());
    }

    public final <T extends GuiTextField> T addOnTextInsertedListener(Consumer<TextInsertedEvent> listener) {
        attachListener(TextInsertedEvent.class, listener); return (T)this;
    }

    //todo: reduce. really needs..   ?
    public static class TextInsertedEvent extends GuiEvent implements Cancellable { // should be TextChangeEvent, lots times text changed but not insert anything
        private String insertedText;

        public TextInsertedEvent(String insertedText) {
            this.insertedText = insertedText;
        }

        public String getInsertedText() {
            return insertedText;
        }
    }
}
