package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.util.Colors;

import java.util.function.Predicate;

public class GuiTextFieldNumerical extends GuiTextField {

    private boolean isNumDragDragging;
    private Gui guiNumDrag = addGui(new Gui()).addOnDraggingListener(e -> {
        float FACT = 0.05f;
        if (Outskirts.isCtrlKeyDown())
            FACT = 2f;

        float diff = -e.getDY() * FACT;
        setText(String.valueOf(getValue()+diff));
    }).setWidth(16).addOnDraggingStateChangedListener(e -> {
        isNumDragDragging = e.isDragging();
    }).addOnDrawListener(e -> {
        drawRect(isNumDragDragging?Colors.WHITE40:e.gui().isMouseOver()?Colors.WHITE30:Colors.WHITE20, e.gui().getX(), e.gui().getY(), e.gui().getWidth(), e.gui().getHeight());
        drawString("↕", e.gui().getX()+4, e.gui().getY()+getHeight()/2f-10, isNumDragDragging?Colors.YELLOW:Colors.WHITE);
    });

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
    }

    private float cachedValue = 0;
    public final float getValue() {
        return cachedValue;
    }

}
