package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.Maths;

import java.util.function.Consumer;

public class GuiSlider extends GuiText {

    /**
     * between 0.0 - 1.0
     */
    private float value = 0;

    private float userMinValue = 0;
    private float userMaxValue = 100;

    private Gui dragGui = addGui(new Gui() {
        @Override public float getX() {
            return GuiSlider.this.getX() + (int)(value*(GuiSlider.this.getWidth()-getWidth()));
        }
        @Override public float getY() {
            return GuiSlider.this.getY();
        }
        @Override public float getWidth() {
            return 10;
        }
        @Override public float getHeight() {
            return GuiSlider.this.getHeight();
        }
    }).addOnDraggingListener((dx,dy) -> {
        setValue(calculateCurrentCursorValue());
    });

    {
        setWidth(100);
        setHeight(16);
        getTextColor().set(Colors.GRAY);

        addOnClickListener(e -> {
            GuiButton.playClickSound();
            setValue(calculateCurrentCursorValue());
        });
        addOnDrawListener(e -> {
            getTextColor().set(isMouseOver() ? Colors.YELLOW : Colors.WHITE);

//            GuiButton.drawButtonTexture(GuiButton.TEXTURE_BUTTON_DISABLE, getX(), getY(), getWidth(), getHeight());
            drawRect(isMouseOver()?Colors.GRAY:Colors.BLACK, getX(), getY()+7, getWidth(), 2);
//            if (isMouseOver())
//                drawRect(Colors.WHITE05, getX(), getY(), getWidth(), getHeight());

            // draw dragGui
            GuiButton.drawButtonTexture(dragGui.isMouseOver()?GuiButton.TEXTURE_BUTTON_HOVER:GuiButton.TEXTURE_BUTTON_NORMAL,
                    dragGui.getX(), dragGui.getY(), dragGui.getWidth(), dragGui.getHeight());
        });
        addOnTextChangedListener(e -> {
            updateTextToCenter(this);
        });
    }

    public GuiSlider() {}

    public GuiSlider(String text) {
        super(text);
    }

    private float calculateCurrentCursorValue() {
        return (Outskirts.getMouseX() - dragGui.getWidth()/2 - getX()) / (getWidth()-dragGui.getWidth());
    }

    public float getUserMaxValue() {
        return userMaxValue;
    }
    public GuiSlider setUserMaxValue(float userMaxValue) {
        this.userMaxValue = userMaxValue;
        return this;
    }

    public float getUserMinValue() {
        return userMinValue;
    }
    public GuiSlider setUserMinValue(float userMinValue) {
        this.userMinValue = userMinValue;
        return this;
    }

    public final GuiSlider setUserMinMaxValue(float umin, float umax) {
        return setUserMinValue(umin).setUserMaxValue(umax);
    }

    public final float getCurrentUserValue() {
        return Maths.lerp(value, getUserMinValue(), getUserMaxValue());
    }
    public GuiSlider setCurrentUserValue(float userValue) {
        setValue(Maths.inverseLerp(userValue, getUserMinValue(), getUserMaxValue()));
        return this;
    }

    public float getValue() {
        return value;
    }
    public GuiSlider setValue(float value) {
        float oldValue = this.value;
        this.value = Maths.clamp(value, 0.0f, 1.0f);
        if (oldValue != this.value) {
            performEvent(new ValueChangedEvent());
        }
        return this;
    }

    public final <T extends GuiSlider> T addValueChangedListener(Consumer<ValueChangedEvent> listener) {
        attachListener(ValueChangedEvent.class, listener); return (T)this;
    }

    public static class ValueChangedEvent extends GuiEvent { }
}
