package outskirts.client.gui.screen;

import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiSlider;
import outskirts.util.Colors;
import outskirts.util.Maths;

import static java.lang.Float.NaN;

public class GuiScreenOptions extends GuiScreen {

    public static final GuiScreenOptions INSTANCE = new GuiScreenOptions();

    private GuiScreenOptions() {
        addOnDrawListener(e -> {
            drawRect(Colors.BLACK40, 0, 0, Outskirts.getWidth(), Outskirts.getHeight());

            drawString("Options", Outskirts.getWidth()/2f, 32, Colors.WHITE, 16, true);
        });
    }

    private GuiButton btnDone = addGui(new GuiButton("Done")); {
        btnDone.addOnClickListener(e -> {
            Outskirts.getRootGUI().removeGui(this);
        });
        btnDone.addLayoutorAlignParentRR(0.5f, NaN);
        btnDone.addLayoutorAlignParentLTRB(NaN, NaN, NaN, 80);
    }

    private GuiSlider slFov = addGui(new GuiSlider()); {
        slFov.addOnValueChangedListener(e -> {
            ClientSettings.FOV = (int)slFov.getCurrentUserValue();

//            slider.setText("FOV: " + ClientSettings.FOV);
        });
        slFov.setUserMinValue(25);
        slFov.setUserMaxValue(115);
        slFov.setCurrentUserValue(ClientSettings.FOV);
        slFov.addOnDrawListener(e -> {
            slFov.addLayoutorAlignParentRR(0.5f, NaN);
            slFov.setY(80);
        });
    }

    private GuiButton btnMusicAndSounds = addGui(new GuiButton("Music & Sounds...")); {
        btnMusicAndSounds.addLayoutorAlignParentRR(0.5f, NaN);
        btnMusicAndSounds.setY(140);
    }

    private GuiButton btnVideoSettings = addGui(new GuiButton("Video Settings...")); {
        btnVideoSettings.addLayoutorAlignParentRR(0.5f, NaN);
        btnVideoSettings.setY(200);
    }

    private GuiSlider slGuiscale = addGui(new GuiSlider()); {
        slGuiscale.addOnValueChangedListener(e -> {
            ClientSettings.GUI_SCALE = Maths.floor(slGuiscale.getCurrentUserValue(), 0.1f);
//            slGuiscale.setText("GUI_SCALE: " + ClientSettings.GUI_SCALE);
        });
        slGuiscale.setUserMinValue(0.5f);
        slGuiscale.setUserMaxValue(4);
        slGuiscale.setCurrentUserValue(ClientSettings.GUI_SCALE);
        slGuiscale.addLayoutorAlignParentRR(0.5f, NaN);
        slGuiscale.setY(370);
    }

}
