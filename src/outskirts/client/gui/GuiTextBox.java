package outskirts.client.gui;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.Texture;
import outskirts.client.render.renderer.gui.FontRenderer;
import outskirts.event.Cancellable;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2i;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;

public class GuiTextBox extends Gui {

    private static final Texture TEX_TEXTBOX_BACKGROUND = Loader.loadTexture(new Identifier("textures/gui/textbox/background.png").getInputStream());
    private static final Texture TEX_TEXTBOX_BACKGROUND_HOVER = Loader.loadTexture(new Identifier("textures/gui/textbox/background_hover.png").getInputStream());

    private int cursorPosition;

    private int selectionBegin;
    private int selectionEnd;

    private long lastFocusedTime;

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

        addCharInputListener(e -> {
            if (isFocused()) {
                insertText(Character.toString(e.getChar()));
            }
        });

        addOnDrawListener(e -> {
            drawCornerStretchTexture(isFocused() ? TEX_TEXTBOX_BACKGROUND_HOVER : TEX_TEXTBOX_BACKGROUND, this, 8);

            if (getCursorPosition() > texts().length())
                setCursorPosition(getCursorPosition()); // clamp/checks cursor position in texts. some times cursorposition had been customed, but then text been setted to empty...

            // set cursor display position
            Vector2i cursorPos = Outskirts.renderEngine.getFontRenderer().calculateTextPosition(texts(), getText().getTextHeight(), getCursorPosition(), null);
            cursor.setX(getText().getX() + cursorPos.x);
            cursor.setY(getText().getY() + cursorPos.y);
            cursor.setHeight(getText().getTextHeight());

            // draw selection
            Vector2i TMP_CACHE = new Vector2i();
            for (int i = getMinSelection();i < getMaxSelection();i++) {
                Vector2i pos = Outskirts.renderEngine.getFontRenderer().calculateTextPosition(texts(), getText().getTextHeight(), i, TMP_CACHE);
                int charWidth = (int)(Outskirts.renderEngine.getFontRenderer().charWidth(texts().charAt(i)) * getText().getTextHeight());
                drawRect(Colors.WHITE20, getText().getX() + pos.x, getText().getY() + pos.y, charWidth + FontRenderer.GAP_CHAR, getText().getTextHeight());
            }
        });

        getText().addOnTextChangedListener(e -> {
            setSelectionBegin(Maths.clamp(getSelectionBegin(), 0, texts().length()));
            setSelectionEnd(Maths.clamp(getSelectionEnd(),     0, texts().length()));
        });
    }

    private int calculateCurrentCursorPosition() {
        return Outskirts.renderEngine.getFontRenderer().calculateTextIndex(texts(), getText().getTextHeight(),
                Outskirts.getMouseX() - getText().getX(),
                Outskirts.getMouseY() - getText().getY());
    }

    public GuiText getText() {
        return text;
    }
    private String texts() {
        return getText().getText();
    }

    public void insertText(String text) {
        if (isSelectedText()) {
            getText().setText(texts().substring(0, getMinSelection()) + text + texts().substring(getMaxSelection()));
            setCursorPosition(getMinSelection() + text.length());

            setSelectionEmpty();
        } else {
            getText().setText(texts().substring(0, getCursorPosition()) + text + texts().substring(getCursorPosition()));
            setCursorPosition(getCursorPosition() + text.length());
        }
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
        setFocused(true);
        this.cursorPosition = Maths.clamp(cursorPosition, 0, texts().length());
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
        return texts().substring(getMinSelection(), getMaxSelection());
    }

}
