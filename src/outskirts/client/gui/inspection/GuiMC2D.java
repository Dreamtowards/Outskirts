package outskirts.client.gui.inspection;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.event.EventHandler;
import outskirts.util.Colors;

public class GuiMC2D extends Gui {

    private boolean[][] data = new boolean[10][10];

    public GuiMC2D() {


        addOnDrawListener(this::onDlaw);
    }

    @EventHandler
    private void onDlaw(OnDrawEvent event) {
        float gridsz = 60;
        float bx = getX(), by = getY();

        drawRect(Colors.BLACK40, this);

        for (int x = 0;x < 10;x++) {
            drawRect(Colors.GRAY, bx+x*gridsz, by, 2, getHeight());
            for (int y = 0;y < 10;y++) {
                float px = bx+x*gridsz, py = by+getHeight()-y*gridsz;
                if (x == 0)
                    drawRect(Colors.GRAY, bx, py-2, getWidth(), 2);

                if (data[x][y]) {
                    float psz = 10;
                    drawRect(Colors.GREEN, px-psz/2+1, py-psz+psz/2-1, psz, psz);
                }
                if (Gui.isMouseOver(px, py-gridsz, gridsz, gridsz)) {
                    drawRect(Colors.WHITE40, px, py-gridsz, gridsz, gridsz);
                    if (Outskirts.isMouseDown(0)) {
                        data[x][y] = true;
                    } else if (Outskirts.isMouseDown(1)) {
                        data[x][y] = false;
                    }
                }
            }
        }

    }
}
