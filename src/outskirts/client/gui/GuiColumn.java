package outskirts.client.gui;

import outskirts.client.gui.Gui;
import outskirts.event.EventHandler;

// GuiLayoutor
public class GuiColumn extends Gui {

    public GuiColumn() {
//        setWrapChildren(true);
        addOnLayoutListener(this::onLayout0);
    }

    @EventHandler
    private void onLayout0(OnLayoutEvent event) {
        float dy=0;
        for (Gui g : getChildren()) {
            g.setRelativeXY(0, dy);
            dy += g.getHeight();
        }
    }

}
