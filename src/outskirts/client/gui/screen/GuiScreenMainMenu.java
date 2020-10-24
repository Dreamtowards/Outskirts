package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiTextBox;
import outskirts.client.gui.ex.GuiIngame;
import outskirts.client.render.renderer.gui.GuiRenderer;
import outskirts.util.Colors;
import outskirts.world.WorldClient;

public class GuiScreenMainMenu extends GuiScreen {

    public static GuiScreenMainMenu INSTANCE = new GuiScreenMainMenu();

//    private GuiButton btnContinuePlay = addGui(new GuiButton("Continue")).addOnClickListener(e -> {
//        Log.info("Unsupported.");
//    });

    public GuiScreenMainMenu() {
        addOnDrawListener(e -> {
            // todo: use modifier
            int baseLineX = (int)(Outskirts.getWidth() * 0.08f);
            int startY = (int)(Outskirts.getHeight() * 0.45f);

            GuiRenderer.OP_roundradius = 2;
            Gui.drawRect(Colors.BLACK, baseLineX-45, 10, 100, 100);
            tbTitle.setX(baseLineX);
            tbTitle.setY(64);
            tbTitle.setVisible(false);  // DEBUG

            btnMultiplayer.setX(baseLineX);
            btnMultiplayer.setY(startY+=60);
            btnOptions.setX(baseLineX);
            btnOptions.setY(startY+=60);
            btnExit.setX(baseLineX);
            btnExit.setY(startY+=60);
        });
    }

    private GuiTextBox tbTitle = addGui(new GuiTextBox()); {
        tbTitle.getText().setTextHeight(32);
        tbTitle.getText().setText("ots");
        tbTitle.setHeight(100);
        tbTitle.setWidth(100);
    }

    private GuiButton btnMultiplayer = addGui(new GuiButton("Connection")); {
        btnMultiplayer.addOnClickListener(e -> {
            if (Outskirts.isShiftKeyDown()) {

                Outskirts.getRootGUI().addGui(GuiScreenConnecting.connect("localhost:25585"));
            } else {

                // tmp offline.

                Outskirts.getRootGUI().removeAllGuis();
                Outskirts.getRootGUI().addGui(GuiIngame.INSTANCE);

                Outskirts.setWorld(new WorldClient());

                Outskirts.getWorld().addEntity(Outskirts.getPlayer());
            }
        });
    }

    private GuiButton btnOptions = addGui(new GuiButton("Options")); {
        btnOptions.addOnClickListener(e -> {
            Outskirts.getRootGUI().addGui(GuiScreenOptions.INSTANCE);
        });
    }

    private GuiButton btnExit = addGui(new GuiButton("Exit")); {
        btnExit.addOnClickListener(e -> {
            Outskirts.shutdown();
        });
    }

}
