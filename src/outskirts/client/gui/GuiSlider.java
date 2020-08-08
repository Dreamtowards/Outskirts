package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.event.EventBus;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.Maths;

import java.util.function.Consumer;

public class GuiSlider extends Gui {

    /**
     * between 0.0 - 1.0
     */
    private float value = 0;

    private float userMinValue = 0;
    private float userMaxValue = 100;

    private Gui dragGui = addGui(new Gui()); {
        dragGui.addOnDraggingListener((dx, dy) -> {
            setValue(calculateCurrentCursorValue());
            dragGui.setRelativeX( value*(getWidth()-dragGui.getWidth()) );
        });
        dragGui.setWidth(10);
        dragGui.setHeight(16);
    }

    public GuiSlider() {
        setWidth(100);
        setHeight(16);

        addOnClickListener(e -> {
            GuiButton.playClickSound();
            setValue(calculateCurrentCursorValue());
        });
        addOnDrawListener(e -> {
//            GuiButton.drawButtonTexture(GuiButton.TEXTURE_BUTTON_DISABLE, getX(), getY(), getWidth(), getHeight());
            drawRect(isMouseOver()?Colors.GRAY:Colors.BLACK, getX(), getY()+7, getWidth(), 2);
//            if (isMouseOver())
//                drawRect(Colors.WHITE05, getX(), getY(), getWidth(), getHeight());

            // draw dragGui
            GuiButton.drawButtonTexture(dragGui.isMouseOver()?GuiButton.TEXTURE_BUTTON_HOVER:GuiButton.TEXTURE_BUTTON_NORMAL,
                    dragGui.getX(), dragGui.getY(), dragGui.getWidth(), dragGui.getHeight());
        });
    }

    private float calculateCurrentCursorValue() {
        return (Outskirts.getMouseX() - getX() - dragGui.getWidth()/2) / (getWidth()-dragGui.getWidth());
    }

    public float getUserMaxValue() {
        return userMaxValue;
    }
    public void setUserMaxValue(float userMaxValue) {
        this.userMaxValue = userMaxValue;
    }
    public float getUserMinValue() {
        return userMinValue;
    }
    public void setUserMinValue(float userMinValue) {
        this.userMinValue = userMinValue;
    }
    public final void setUserMinMaxValue(float umin, float umax) {
        setUserMinValue(umin);
        setUserMaxValue(umax);
    }

    public final float getCurrentUserValue() {
        return Maths.lerp(value, getUserMinValue(), getUserMaxValue());
    }
    public void setCurrentUserValue(float userValue) {
        setValue(Maths.inverseLerp(userValue, getUserMinValue(), getUserMaxValue()));
    }

    public float getValue() {
        return value;
    }
    public void setValue(float value) {
        float oldValue = this.value;
        this.value = Maths.clamp(value, 0.0f, 1.0f);
        if (oldValue != this.value) {
            performEvent(new ValueChangedEvent());
        }
    }

    public final EventBus.Handler addValueChangedListener(Consumer<ValueChangedEvent> listener) {
        return attachListener(ValueChangedEvent.class, listener);
    }

    public static class ValueChangedEvent extends GuiEvent { }
}
