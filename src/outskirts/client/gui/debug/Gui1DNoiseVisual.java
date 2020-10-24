package outskirts.client.gui.debug;

import outskirts.client.gui.Gui;
import outskirts.event.EventHandler;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;
import outskirts.world.gen.NoiseGeneratorPerlin;

public class Gui1DNoiseVisual extends Gui {

    {
        addOnDrawListener(this::dlaw);
    }

    private NoiseGeneratorPerlin noise = new NoiseGeneratorPerlin();

    @EventHandler
    private void dlaw(OnDrawEvent event) {
        int UNIT_WIDTH = 200;
        Gui.drawRect(Colors.DARK_GRAY, this);
        Gui.drawRect(Colors.RED, getX(), getY()+getHeight()/2f, getWidth(),1);  // BASELINE
        Gui.drawRect(Colors.DARK_RED,   getX(), getY(), getWidth(),1);  // TOP
        Gui.drawRect(Colors.DARK_RED,   getX(), getY()+getHeight(), getWidth(),1);  // BOTTOM

        for (int x = 0;x < getWidth();x++) {
            float v = noise.fbm(x/(float)UNIT_WIDTH, 4);
            float usz = 0.2f;
            float flr = Maths.floor(v, usz);
            v = flr+ (flr+usz)-v;

            float y = v*(getHeight()/2f);
            Gui.drawRect(Colors.GREEN, getX()+x, getY()+getHeight()/2f+ -y, 2,2);

            if (x % UNIT_WIDTH == 0)  // dlaw UNIT.
                Gui.drawRect(Colors.RED, getX()+x, getY()+getHeight()/2f-5, 2, 10);
        }
    }

}
