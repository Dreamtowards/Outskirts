package outskirts.client.gui.inspection.num;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiDrag;
import outskirts.client.gui.GuiTextBox;
import outskirts.event.EventPriority;
import outskirts.util.Colors;
import outskirts.util.logging.Log;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.Float.NaN;

public class GuiIScalar extends GuiTextBox {

    private float cachedvalue;

    /**
     * deltaValue = numDragPixel * dragSensitivity
     */
    private float dragSensitivity = 0.2f;

    private Function<Float, Float> valuefilter = Function.identity();

    public GuiIScalar(Supplier<Float> get, Consumer<Float> set) {
        setHeight(22);
        setWidth(80);
        getText().setRelativeY(3);
        getText().setText("0");


        getText().addOnTextChangeListener(e -> {
            try {
                cachedvalue = Float.parseFloat(e.getNewText());
            } catch (NumberFormatException ex) {
                e.setCancelled(true); return;
            }
            cachedvalue = valuefilter.apply(cachedvalue);
            e.setNewText(Float.toString(cachedvalue));
            if (get.get() != cachedvalue) {
                set.accept(cachedvalue);
            }
        });
        addOnDrawListener(e -> {
            float rv = get.get();
            if (rv != getValue()) {
                setValue(rv);
            }
        }).priority(EventPriority.HIGH);

        addChildren(new GuiDrag().exec((GuiDrag g) -> {
            g.setWidth(10);
            g.addLayoutorAlignParentLTRB(NaN, 0, 0, 0);
            g.addOnDraggingListener(e -> {
                setValue(getValue() + -e.dy*getDragSensitivity());
                setSelectionEmpty();
            });
            g.addOnDrawListener(e -> {
                drawRect(g.isDragging() ? Colors.WHITE40 :
                        g.isHover() ? Colors.WHITE20 : Colors.BLACK40, g);
            });
        }));
    }

    public float getValue() {
        return cachedvalue;
    }

    public void setValue(float v) {
        getText().setText(Float.toString(v));
    }

    public void setValueFilter(Function<Float, Float> valuefilter) {
        this.valuefilter = valuefilter;
    }

    public float getDragSensitivity() {
        return dragSensitivity;
    }
    public void setDragSensitivity(float dragSensitivity) {
        this.dragSensitivity = dragSensitivity;
    }
}
