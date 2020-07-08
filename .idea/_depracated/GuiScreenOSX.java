package outskirts.client.gui.screen.tools;

import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.client.gui.screen.GuiScreen;
import outskirts.client.gui.screen.options.GuiScreenOptions;
import outskirts.util.Colors;
import outskirts.util.SystemUtils;

public class GuiScreenOSX extends GuiScreen {

    private GuiSlider slider = addGui(new GuiSlider().setX(10).setY(200).setWidth(100).setHeight(20));

    private GuiText sysText = new GuiText().setText("System").addOnDrawListener(e -> {
        if (e.gui().isMouseOver()) {
            drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());
        }
    }).setHeight(20).setWidth(100);

    private GuiMenu sysMenu = new GuiMenu();

    private Gui toolbar = addGui(new Gui()).addOnDrawListener(e -> {
        drawRect(Colors.WHITE20, getX(), getY(), getWidth(), getHeight());
    });

    public GuiScreenOSX() {

        toolbar.addGui(sysMenu);
        toolbar.addGui(sysText);

        setWidth(Outskirts.getWidth()).setHeight(Outskirts.getHeight());

        sysMenu.setWidth(100);
        sysText.addOnClickListener(e -> {
            sysMenu.show(sysText.getX(), sysText.getY() + sysText.getHeight());
        });


//        GuiMenu.GuiMenuItem itemShutdown = new GuiMenu.GuiMenuItem("Shutdown");
//        sysMenu.getItemsGui().addGui(itemShutdown);
//
//        GuiMenu.GuiMenuItem itemRestart = new GuiMenu.GuiMenuItem("Restart");
//        sysMenu.getItemsGui().addGui(itemRestart);
//
//        GuiMenu.GuiMenuItem itemLogout = new GuiMenu.GuiMenuItem("Logout");
//        {
//            GuiMenu lo = new GuiMenu().setWidth(130);
//            lo.getItemsGui().addGui(new GuiMenu.GuiMenuItem("FastLogout"));
//            lo.getItemsGui().addGui(new GuiMenu.GuiMenuItem("NonFastLogout"));
//            itemLogout.setSubMenu(lo);
//            GuiMenu.GuiMenuItem ano = new GuiMenu.GuiMenuItem("More");
//            {
//                GuiMenu lo2 = new GuiMenu().setWidth(100);
//                lo2.getItemsGui().addGui(new GuiMenu.GuiMenuItem("Fast").addOnClickListener(e -> {
//
//                }));
////                lo2.getItemsGui().addGui(new GuiMenu.GuiMenuItem("Options").addOnClickListener(e -> Outskirts.displayScreen(new GuiScreenOptions())));
//                ano.setSubMenu(lo2);
//            }
//            lo.getItemsGui().addGui(ano);
//        }
//        sysMenu.getItemsGui().addGui(itemLogout);

        toolbar.setWidth(Outskirts.getWidth()).setHeight(20);
    }
}
