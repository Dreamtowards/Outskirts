package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.event.Event;
import outskirts.event.EventBus;
import outskirts.event.EventPriority;
import outskirts.event.gui.GuiEvent;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GuiSlider extends Gui {

    private static final Vector4f COLOR_FILLED = Colors.fromRGB(127, 127, 127);
    private static final Vector4f COLOR_UNFILLED = Colors.fromRGB(64, 64, 64);

    /**
     * between 0.0 - 1.0
     */
    private float value = 0;

    private float userMinValue = 0;
    private float userMaxValue = 100;

    private Set<Float> userOptionalValues = new HashSet<>();

    private GuiDrag dragGui = addGui(new GuiDrag()); {
        dragGui.addOnDraggingListener(e -> {
            float f = calculateCurrentCursorValue();
            for (float v : getUserOptionalValues()) {
                float percen = Maths.inverseLerp(v, getUserMinValue(), getUserMaxValue());
                if (Math.abs(percen*getWidth() - f*getWidth()) < 9)
                    f = percen;
            }
            setValue(f);
        });
        dragGui.addOnLayoutListener(e -> {
            dragGui.setWidth(getHeight()*0.64f);
            dragGui.setHeight(getHeight());
        });
    }

    public GuiSlider() {
        setWidth(180);
        setHeight(28);

        GuiButton.initOnMouseDownClickSound(this);

        addOnPressedListener(e -> {
            dragGui.setDragging(true);
            dragGui.setPressed(true);
            setValue(calculateCurrentCursorValue());
        });

        addOnDrawListener(e -> {
            drawRect(isHover()||isPressed()?Colors.WHITE:Colors.BLACK, getX(), getY()+4, getWidth(), getHeight()-8); // Background Border
            drawRect(COLOR_UNFILLED, getX()+2, getY()+6, getWidth()-4, getHeight()-12); // Unfilled Background
            drawRect(COLOR_FILLED, getX()+2, getY()+6, dragGui.getRelativeX(), getHeight()-12);  // Filled

            // User Optional Values
            for (float v : getUserOptionalValues()) {
                float WIDTH = 4;
                float percen = Maths.inverseLerp(v, getUserMinValue(), getUserMaxValue());
                drawRect(percen<getValue()?Colors.BLACK40:Colors.WHITE40, getX()+ percen*(getWidth()-dragGui.getWidth())-WIDTH/2+dragGui.getWidth()/2, getY()+9, WIDTH, getHeight()-18);
            }

            // draw dragGui
//            GuiButton.drawButtonBackground(dragGui);
            drawCornerStretchTexture(isPressed()||dragGui.isHover()? GuiButton.TEX_BUTTON_BACKGROUND_HOVER : GuiButton.TEX_BUTTON_BACKGROUND, dragGui, 6);

            // dlaw UserValue
            if (isPressed()||dragGui.isHover()) {
                float midX = dragGui.getX() + dragGui.getWidth()/2f;
                float bgWidth = 100;
                float bgHeight = 24;
                float margin = 4;
                float bgY = dragGui.getY()-bgHeight-margin;
                drawRect(Colors.BLACK40, midX-bgWidth/2f, bgY, bgWidth, bgHeight);
                drawString(String.valueOf(getCurrentUserValue()), midX, bgY+3, Colors.YELLOW, 16, .5f);
            }

            if (isPressed()) {
                drawRect(Colors.WHITE10, dragGui);
            }

//            if (dragGui.isHover())drawString(getCurrentUserValue()+"", dragGui.getX()+dragGui.getWidth()/2, getY()-20, Colors.WHITE, 16, true);
        });
    }

    private float calculateCurrentCursorValue() {
        return (Outskirts.getMouseX() - dragGui.getWidth()/2 - getX()) / (getWidth()-dragGui.getWidth());
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

    public float toUserValue(float t) {
        return Maths.lerp(t, getUserMinValue(), getUserMaxValue());
    }
    public float fromUserValue(float userval) {
        return Maths.inverseLerp(userval, getUserMinValue(), getUserMaxValue());
    }

    public final float getCurrentUserValue() {
        return toUserValue(value);
    }
    public final void setCurrentUserValue(float userValue) {
        setValue(fromUserValue(userValue));
    }

    public float getValue() {
        return value;
    }
    public void setValue(float value) {
        float oldValue = this.value;
        OnValueChangeEvent e = new OnValueChangeEvent(value, this);
        performEvent(e);
        this.value = Maths.clamp(e.getNewValue(), 0.0f, 1.0f);
        if (oldValue != this.value) {
            performEvent(new OnValueChangedEvent());

            dragGui.setRelativeX( getValue()*(getWidth()-dragGui.getWidth()) );
        }
    }

    public Set<Float> getUserOptionalValues() {
        return userOptionalValues;
    }

    public final EventBus.Handler addOnValueChangedListener(Consumer<OnValueChangedEvent> listener) {
        return attachListener(OnValueChangedEvent.class, listener);
    }

    public final EventBus.Handler addOnValueChangeListener(Consumer<OnValueChangeEvent> lsr) {
        return attachListener(OnValueChangeEvent.class, lsr);
    }

    public final void initOnlyIntegerValues() {
        addOnValueChangeListener(e -> {
            e.setNewUserValue(Maths.floor(e.getNewUserValue()));
        });
    }

    public final void initValueSync(Supplier<Float> get, Consumer<Float> set) {
        addOnValueChangedListener(e -> {
            float thisf = getCurrentUserValue();
            if (get.get() != thisf) {
                set.accept(thisf);
            }
        });
        addOnDrawListener(e -> {
            float f = get.get();
            if (f != getCurrentUserValue()) {
                setCurrentUserValue(f);
            }
        }).priority(EventPriority.HIGH);
    }

    public static class OnValueChangedEvent extends GuiEvent { }

    public static class OnValueChangeEvent extends GuiEvent {
        private float newValue;
        private GuiSlider sldptr;
        private OnValueChangeEvent(float newValue, GuiSlider sldptr) {
            this.newValue = newValue;
            this.sldptr = sldptr;
        }
        public float getNewValue() {
            return newValue;
        }
        public void setNewValue(float newValue) {
            this.newValue = newValue;
        }
        public float getNewUserValue() {
            return sldptr.toUserValue(getNewValue());
        }
        public void setNewUserValue(float newUserValue) {
            setNewValue(sldptr.fromUserValue(newUserValue));
        }
    }
}
