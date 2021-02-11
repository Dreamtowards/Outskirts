package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiExpander;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.util.Colors;

import java.util.function.Consumer;

import static outskirts.util.logging.Log.LOGGER;

public class GuiDebugSnapshot extends Gui {


    public GuiDebugSnapshot(Gui g) {
        addGui(doTree(g));
    }

    private static Gui doTree(Gui gui) {
        return new GuiExpander(String.format("%s%s [%s]", gui.getClass().getSimpleName(), gui.isVisible()?"":" (-)", gui.size())).exec((GuiExpander g) -> {
            g.getHeader().addOnDrawListener(e -> {
                if (g.getHeader().isHover()) {
                    Outskirts.getRootGUI().addOnPostDrawListener(new Consumer<OnPostDrawEvent>() {
                        @Override
                        public void accept(OnPostDrawEvent onPostDrawEvent) {
                            drawRect(Colors.WHITE40, gui);
                            Outskirts.getRootGUI().removeListeners(this);
                        }
                    });
                }
            });
        }).setContent(new GuiColumn().exec(g -> {
            g.setRelativeX(10);
            for (Gui child : gui.getChildren()) {
                g.addGui(doTree(child));
            }
        }));
    }
}
