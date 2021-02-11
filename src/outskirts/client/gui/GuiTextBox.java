package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Texture;
import outskirts.client.render.renderer.gui.FontRenderer;
import outskirts.event.EventBus;
import outskirts.event.client.input.CharInputEvent;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;

public class GuiTextBox extends Gui {

    private static final Texture TEX_TEXTBOX_BACKGROUND = Loader.loadTexture(new Identifier("textures/gui/textbox/background.png").getInputStream());
    private static final Texture TEX_TEXTBOX_BACKGROUND_HOVER = Loader.loadTexture(new Identifier("textures/gui/textbox/background_hover.png").getInputStream());

    private int cursorPosition;

    private int selectionBegin;
    private int selectionEnd;

    private long lastFocusedTime;

    private int maxLines = Integer.MAX_VALUE;

    private GuiText text = addGui(new GuiText());

    private Gui cursor = addGui(new Gui()); {
        cursor.setWidth(3);
        cursor.addOnDrawListener(e -> {
            if (isFocused() && ((System.currentTimeMillis() / 500) % 2 == 0 || lastFocusedTime > System.currentTimeMillis() - 600)) {
                drawRect(Colors.WHITE, cursor);
            }
        });
    }


    public GuiTextBox() {
        this("");
    }

    public GuiTextBox(String s) {
        getText().setText(s);
        getText().setRelativeXY(8, 8);

        setWidth(180);
        setHeight(32);

        addOnPressedListener(e -> {
            int cursorPos = calculateCurrentCursorPosition();
            if (cursorPos != -1) {
                setCursorPosition(cursorPos);
                setSelectionBegin(cursorPos);
                setSelectionEnd(cursorPos);
            }
        });

        addMouseMoveListener(e -> {
            if (isPressed()) {  // || (e.getMouseButton() == 0 && !e.getButtonState())
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
                        setSelectionEnd(texts().length());
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

        addGlobalEventListener(CharInputEvent.class, e -> {
            if (isFocused()) {
                insertText(Character.toString(e.getChar()));
            }
        });

        addOnDrawListener(e -> {
            drawCornerStretchTexture(isFocused() ? TEX_TEXTBOX_BACKGROUND_HOVER : TEX_TEXTBOX_BACKGROUND, this, 6); // 8

        }).unregisterTag(EVTAG_DEFDECO);

        addOnDrawListener(e -> {

            assert getCursorPosition() <= texts().length() : "cpos:"+getCursorPosition()+", texlen:"+texts().length();
            // set cursor display position
            Vector2f cursorPos = calculateTextPosition(getCursorPosition());
            cursor.setX(cursorPos.x);
            cursor.setY(cursorPos.y);
            cursor.setHeight(getText().getTextHeight());

            // draw selection
            Vector2f TMP_CACHE = new Vector2f();
            for (int i = getMinSelection();i < getMaxSelection();i++) {
                Vector2f pos = Outskirts.renderEngine.getFontRenderer().calculateTextPosition(texts(), getText().getTextHeight(), i, TMP_CACHE);
                float charWidth = Outskirts.renderEngine.getFontRenderer().charWidth(texts().charAt(i)) * getText().getTextHeight();
                drawRect(Colors.WHITE20, getText().getX() + pos.x, getText().getY() + pos.y, charWidth + FontRenderer.OP_CHAR_GAP, getText().getTextHeight()+FontRenderer.OP_LINE_GAP);
            }
        });

        getText().addOnTextChangedListener(e -> {
            setSelectionBegin(Maths.clamp(getSelectionBegin(), 0, texts().length()));
            setSelectionEnd(Maths.clamp(getSelectionEnd(),     0, texts().length()));

            if (getCursorPosition() > texts().length())
                setCursorPosition(getCursorPosition()); // clamp/checks cursor position in texts. some times cursorposition had been customed, but then text been setted to empty...
        });
    }

    public Vector2f calculateTextPosition(int idx) {
        return Outskirts.renderEngine.getFontRenderer()
                .calculateTextPosition(texts(), getText().getTextHeight(), idx, new Vector2f())
                .add(getText().getX(), getText().getY());
    }

    private int calculateCurrentCursorPosition() {
        return Outskirts.renderEngine.getFontRenderer().calculateTextIndex(texts(), getText().getTextHeight(),
                Outskirts.getMouseX() - getText().getX(),
                Outskirts.getMouseY() - getText().getY());
    }

    public GuiText getText() {
        return text;
    }
    public final String texts() {
        return getText().getText();
    }

    public final void insertText(String text) {
        if (isSelectedText()) {  // if selected text, clear first.
            getText().setText(texts().substring(0, getMinSelection()) + texts().substring(getMaxSelection()));
            setCursorPosition(getMinSelection());
            setSelectionEmpty();
        }
        if (maxLines != Integer.MAX_VALUE) {  // detect maxlines.
            if (maxLines==1) { // just simply a little faster simplified usually case.
                text = text.replaceAll("\n","");
            } else {
                int n = 0;
                String strcurr = texts();
                for (int i = 0; i < strcurr.length(); i++) {
                    if (strcurr.charAt(i) == '\n') n++;
                }
                assert n < maxLines;
                for (int i = 0; i < text.length(); i++) {
                    if (text.charAt(i) == '\n') {
                        n++;
                        if (n >= maxLines) {
                            text = text.substring(0, i) + text.substring(i).replaceAll("\n", "");
                            break;
                        }
                    }
                }
            }
        }

        getText().setText(texts().substring(0, getCursorPosition()) + text + texts().substring(getCursorPosition()));
        setCursorPosition(getCursorPosition() + text.length());
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        lastFocusedTime = System.currentTimeMillis();
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int cursorPosition) {
        float oldCpos = this.cursorPosition;
        this.cursorPosition = Maths.clamp(cursorPosition, 0, texts().length());
        if (oldCpos != this.cursorPosition) {
            setFocused(true);
            performEvent(new OnCursorPositionChangedEvent());
        }
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

    public void setSelectionEmpty() {
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
        return texts().substring(getMinSelection(), getMaxSelection());
    }

    public int getMaxLines() {
        return maxLines;
    }
    public void setMaxLines(int maxLines) {
        assert maxLines >= 1;
        this.maxLines = maxLines;
    }

    public Gui getCursor() {
        return cursor;
    }

    public final EventBus.Handler addOnCursorPositionChangedListener(Consumer<OnCursorPositionChangedEvent> lsr) {
        return attachListener(OnCursorPositionChangedEvent.class, lsr);
    }

    public static class OnCursorPositionChangedEvent extends GuiEvent { }
}
