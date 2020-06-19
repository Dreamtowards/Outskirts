package outskirts.client.gui.screen.hawks;

import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiTextField;
import outskirts.util.Colors;

public class GuiHawksSysCMD extends Gui {

    private GuiTextField textBox = addGui(new GuiTextField() {
        @Override
        public float getWidth() {
            return GuiHawksSysCMD.this.getWidth();
        }
        @Override
        public float getHeight() {
            return GuiHawksSysCMD.this.getHeight();
        }
    }.setText("ABC").setTextHeight(32));

    public GuiHawksSysCMD() {

        textBox.addOnTextInsertedListener(e -> {
            if (e.getInsertedText().equals("\n")) {
                //e.setCancelled(true);
            }
        });

        addOnDrawListener(e -> {

            drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());
        });
    }

    @Override
    public float getWidth() {
        return getParent().getWidth();
    }

    @Override
    public float getHeight() {
        return getParent().getHeight();
    }
}
