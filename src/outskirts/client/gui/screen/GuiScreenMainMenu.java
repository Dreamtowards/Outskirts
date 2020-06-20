package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiTextField;
import outskirts.client.render.renderer.GuiRenderer;
import outskirts.util.Colors;
import outskirts.world.WorldClient;

public class GuiScreenMainMenu extends GuiScreen {

    public static GuiScreenMainMenu INSTANCE = new GuiScreenMainMenu();

//    private GuiButton btnContinuePlay = addGui(new GuiButton("Continue")).addOnClickListener(e -> {
//        Log.info("Unsupported.");
//    });

    public GuiScreenMainMenu() {
        addOnDrawListener(e -> {
            int baseLineX = (int)(Outskirts.getWidth() * 0.08f);
            int startY = (int)(Outskirts.getHeight() * 0.45f);

            GuiRenderer.PARAM_roundradius = 2;
            Gui.drawRect(Colors.BLACK, baseLineX-45, 10, 100, 100);
            tbTitle.setX(baseLineX).setY(64);

            btnMultiplayer.setX(baseLineX).setY(startY+=60);
            btnOptions.setX(baseLineX).setY(startY+=60);
            btnExit.setX(baseLineX).setY(startY+=60);
        });
    }

    private GuiTextField tbTitle = addGui(new GuiTextField()).setTextHeight(32).setText("ots").setHeight(100).setWidth(100);

    private GuiButton btnMultiplayer = addGui(new GuiButton("Connection")).addOnClickListener(e -> {
        if (Outskirts.isShiftKeyDown()) {

            Outskirts.startScreen(GuiScreenConnecting.connect("localhost:25585"));

        } else {

            Outskirts.closeAllScreen();

            Outskirts.setWorld(new WorldClient());

            Outskirts.getWorld().addEntity(Outskirts.getPlayer());
        }
    });

    private GuiButton btnOptions = addGui(new GuiButton("Options")).addOnClickListener(e -> {
        Outskirts.startScreen(GuiScreenOptions.INSTANCE);
    });

    private GuiButton btnExit = addGui(new GuiButton("Exit")).addOnClickListener(e -> {
        Outskirts.shutdown();
    });

}
