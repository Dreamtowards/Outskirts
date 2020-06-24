package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.event.EventPriority;
import outskirts.util.Colors;

import java.util.function.Predicate;

public class GuiTextFieldNumerical extends GuiTextField {

    private boolean isNumDragDragging;
    private Gui guiNumDrag = addGui(new Gui()).addOnDraggingListener((dx, dy) -> {
        float FACT = 0.05f;
        if (Outskirts.isCtrlKeyDown())
            FACT = 2f;
        float diff = -dy * FACT;
        setText(String.format("%.2f", getValue()+diff));
    }, isDragging -> {
        isNumDragDragging = isDragging;
        Outskirts.setMouseGrabbed(isDragging);
    }, null).setWidth(16);

    public GuiTextFieldNumerical() {
        setText("0");

        addOnLayoutListener(e -> {
            guiNumDrag.setHeight(getHeight())
                    .setX(getX()+getWidth()-guiNumDrag.getWidth())
                    .setY(getY());

            getTextOffset().set(8, (getHeight()-getTextHeight())/2f);
        });

        addOnTextChangeListener(e -> {
            try {
                cachedValue = Float.parseFloat(e.getNewText());
            } catch (NumberFormatException ex) {
                e.setCancelled(true);
            }
        });

        addOnDrawListener(e -> {
            drawRect(isNumDragDragging?Colors.WHITE40:guiNumDrag.isMouseOver()?Colors.WHITE30:Colors.WHITE20, guiNumDrag);
            drawString("↕", guiNumDrag.getX()+guiNumDrag.getWidth()/2, guiNumDrag.getY()+getHeight()/2f-10, isNumDragDragging?Colors.YELLOW:Colors.WHITE, 16, true, false);

        }, EventPriority.LOWEST);
    }

    private float cachedValue = 0;
    public final float getValue() {
        return cachedValue;
    }

}
