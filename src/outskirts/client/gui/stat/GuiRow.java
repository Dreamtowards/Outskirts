package outskirts.client.gui.stat;

import outskirts.client.gui.Gui;
import outskirts.event.EventHandler;

public class GuiRow extends Gui {

    public GuiRow() {
        setWrapChildren(true);
        //todo: ??  getwidth() -> width == NaN ? childrenbound.x : width=Infinity ? parent.width : width.

        addOnLayoutListener(this::onLayout0);
    }

    @EventHandler
    private void onLayout0(OnLayoutEvent event) {
        float dx=0;
        for (Gui g : getChildren()) {
            g.setRelativeXY(dx, 0);
            dx += g.getWidth();
        }
    }
}