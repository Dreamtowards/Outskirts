package outskirts.client.gui.screen.options;

import outskirts.client.GameSettings;
import outskirts.client.Outskirts;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiLayoutLinear;
import outskirts.client.gui.GuiSlider;
import outskirts.client.gui.screen.GuiScreen;
import outskirts.client.gui.GuiScroll;
import outskirts.client.gui.screen.GuiScreenMainMenu;
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
        GameSettings.FOV = (int)slider.getCurrentUserValue();

        slider.setText("FOV: " + GameSettings.FOV);
    }).setUserMinValue(25).setUserMaxValue(115).setCurrentUserValue(GameSettings.FOV).addOnDrawListener(e -> {
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
        GameSettings.GUI_SCALE = Maths.floor(slider.getCurrentUserValue(), 0.1f);
        slider.setText("GUI_SCALE: " + GameSettings.GUI_SCALE);
    }).setUserMinValue(0.5f).setUserMaxValue(4).setCurrentUserValue(GameSettings.GUI_SCALE).addOnDrawListener(e -> {
        e.gui().setX(Outskirts.getWidth()/2 - e.gui().getWidth()/2);
        e.gui().setY(310+60);
    });

}
