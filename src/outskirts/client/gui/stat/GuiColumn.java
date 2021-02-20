package outskirts.client.gui.stat;

import outskirts.client.gui.Gui;
import outskirts.event.EventHandler;

// GuiLayoutor
public class GuiColumn extends Gui {

    public GuiColumn() {
//        setWrapChildren(true);
        addOnLayoutListener(this::onLayout0);
    }

    @EventHandler
    private void onLayout0(OnLayoutEvent e) {
        float dy=0;
        for (Gui g : getChildren()) {
            g.setRelativeY(dy);
            dy += g.getHeight();
        }
    }

}
