package outskirts.client.gui.screen;

import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiSlider;
import outskirts.util.Colors;
import outskirts.util.Maths;

public class GuiScreenOptions extends GuiScreen {

    public static final GuiScreenOptions INSTANCE = new GuiScreenOptions();

    private GuiScreenOptions() {
        addOnDrawListener(e -> {
            drawRect(Colors.BLACK40, 0, 0, Outskirts.getWidth(), Outskirts.getHeight());

            drawString("Options", Outskirts.getWidth()/2f, 32, Colors.WHITE, 16, true);
        });
    }

    private GuiButton btnDone = addGui(new GuiButton("Done")).addOnClickListener(e -> {
        Outskirts.closeScreen();
    }).addOnDrawListener(e -> { // todo: should have a Resize/ AdjustPosition Event. and this OnDraw'll not working in 1st onDraw.  How about "On(Parent)ResizedEvent"? WindowResize->RootGui.setHeight/Width(), but how about Children resized...
        e.gui().setX(Outskirts.getWidth()/2 - e.gui().getWidth()/2);
        e.gui().setY(Outskirts.getHeight()-80);
    });

    private GuiSlider sldFov = addGui(new GuiSlider()).addValueChangedListener(e -> {
        GuiSlider slider = e.gui();
        ClientSettings.FOV = (int)slider.getCurrentUserValue();

        slider.setText("FOV: " + ClientSettings.FOV);
    }).setUserMinValue(25).setUserMaxValue(115).setCurrentUserValue(ClientSettings.FOV).addOnDrawListener(e -> {
        e.gui().setX(Outskirts.getWidth()/2 - e.gui().getWidth()/2);
        e.gui().setY(140-60);
    });

    private GuiButton btnMusicAndSounds = addGui(new GuiButton("Music & Sounds...")).addOnDrawListener(e -> {
        e.gui().setX(Outskirts.getWidth()/2 - e.gui().getWidth()/2);
        e.gui().setY(140);
    });

    private GuiButton btnVideoSettings = addGui(new GuiButton("Video Settings...")).addOnDrawListener(e -> {
        e.gui().setX(Outskirts.getWidth()/2 - e.gui().getWidth()/2);
        e.gui().setY(200);
    });

    private GuiSlider sldGuiscale = addGui(new GuiSlider()).addValueChangedListener(e -> {
        GuiSlider slider = (GuiSlider)e.gui();
        ClientSettings.GUI_SCALE = Maths.floor(slider.getCurrentUserValue(), 0.1f);
        slider.setText("GUI_SCALE: " + ClientSettings.GUI_SCALE);
    }).setUserMinValue(0.5f).setUserMaxValue(4).setCurrentUserValue(ClientSettings.GUI_SCALE).addOnDrawListener(e -> {
        e.gui().setX(Outskirts.getWidth()/2 - e.gui().getWidth()/2);
        e.gui().setY(310+60);
    });

}
