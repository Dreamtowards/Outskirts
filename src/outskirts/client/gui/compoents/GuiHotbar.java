package outskirts.client.gui.compoents;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.event.EventHandler;
import outskirts.item.stack.ItemStack;
import outskirts.util.Colors;
import outskirts.util.Maths;

public class GuiHotbar extends Gui {

    public GuiHotbar() {

        addOnDrawListener(this::onDlaw);
        addMouseScrollListener(e -> {
            if (Outskirts.isIngame())
                Outskirts.getPlayer().setHotbarSlot(
                     (int)Maths.mod(Outskirts.getPlayer().getHotbarSlot()+Math.signum(Outskirts.getDScroll()), 16));
        });
    }

    @EventHandler
    private void onDlaw(OnDrawEvent e) {
        int SZ = 40;

        setHeight(4*45);
        setWidth(8*45);
        drawRect(Colors.BLACK05, this);


        int i = 0;
        float dx = 0;
        float dy = 0;
        for (ItemStack stack : Outskirts.getPlayer().getBackpackInventory().getItemStacks()) {
            float x=getX()+dx, y=getY()+dy;
            if (!stack.empty()) {
                stack.getItem().drawItem(x,y,SZ,SZ);
                if (stack.amount() != 1)
                    drawString(String.valueOf(stack.amount()), x,y+SZ-16, Colors.WHITE, 16);
                drawString(String.valueOf(i), x,y,Colors.DARK_GRAY);
            } else {
                drawString("EPT", x,y,Colors.GRAY);
            }
            if (Outskirts.getPlayer().getHotbarSlot() == i) {
                drawRect(Colors.WHITE20, x,y,SZ,SZ);
            }
            i++;

            dx+=45;
            if (i % 8 == 0) {
                dx=0;
                dy+=45;
            }
        }
    }
}
