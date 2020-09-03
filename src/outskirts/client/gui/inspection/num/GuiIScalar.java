package outskirts.client.gui.inspection.num;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiDrag;
import outskirts.client.gui.GuiTextBox;
import outskirts.util.Colors;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.Float.NaN;

public class GuiIScalar extends GuiTextBox {

    private float cachedvalue;

    private boolean _notCallSetter = false;

    private float sens = 0.2f;

    public GuiIScalar(Supplier<Float> getter, Consumer<Float> setter) {
        setHeight(22);
        setWidth(80);
        getText().setRelativeY(3);
        getText().setText("0");

        addChildren(new GuiDrag().exec((GuiDrag g) -> {
            g.setWidth(10);
            g.addLayoutorAlignParentLTRB(NaN, 0, 0, 0);
            g.addOnDraggingListener(e -> {
                setValue(getValue() + -Outskirts.getMouseDY()*sens);
                setSelectionEmpty();
            });
            g.addOnDrawListener(e -> {
                drawRect(g.isDragging() ? Colors.WHITE40 :
                         g.isHover() ? Colors.WHITE20 : Colors.BLACK40, g);
            });
        }));

        addOnDrawListener(e -> {
            float remotevalue = getter.get();
            if (remotevalue != getValue()) {
                _notCallSetter=true;
                setValue(remotevalue);
            }
        });


        getText().addOnTextChangeListener(e -> {
            try {
                cachedvalue = Float.parseFloat(e.getNewText());
            } catch (NumberFormatException ex) {
                e.setCancelled(true);
                return;
            }
            if (_notCallSetter) {
                _notCallSetter=false;
                return;
            }
            setter.accept(getValue());
        });
    }

    public float getValue() {
        return cachedvalue;
    }

    public void setValue(float v) {
        getText().setText(Float.toString(v));
    }
}
