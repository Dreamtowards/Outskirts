package outskirts.client.gui.ex;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.util.Colors;

public class GuiIngame extends Gui {

    public GuiIngame() {

        addOnDrawListener(this::onRender);

    }

    private void onRender(OnDrawEvent event) {


        // Pointer. this actually not belong Debug.
        int POINTER_SIZE = 4;
        Gui.drawRect(Colors.WHITE, Outskirts.getWidth()/2f-POINTER_SIZE/2f, Outskirts.getHeight()/2f-POINTER_SIZE/2f, POINTER_SIZE, POINTER_SIZE);


        if (Outskirts.getWorld() != null)
            Gui.drawWorldpoint(Outskirts.getWorld().getEntities().get(1).getPosition(), (x, y) -> {
                Gui.drawString(Outskirts.getPlayer().getName(), x, y, Colors.GRAY);
            });


    }

}
