package outskirts.client.gui.screen;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.event.Events;
import outskirts.event.client.input.KeyboardEvent;
import outskirts.util.Colors;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;

import static java.lang.Float.NaN;
import static java.lang.Float.POSITIVE_INFINITY;

public class GuiScreenPause extends Gui {

    public static GuiScreenPause INSTANCE = new GuiScreenPause();

    public GuiScreenPause() {
        setWidth(INFINITY);
        setHeight(INFINITY);

        initEscClose(this);

        addKeyboardListener(e -> {
//            if (e.getKeyState() && e.getKey() == GLFW.GLFW_KEY_ESCAPE && Outskirts.getRootGUI().getGui(Outskirts.getRootGUI().size()-1) == this) {
//                Outskirts.getRootGUI().removeGui(this);
//            }
            if (e.getKeyState() && e.getKey() == GLFW.GLFW_KEY_R) {
                removeAllGuis();
                build();
            }
        });

        build();
    }

    private void build() {

        addChildren(
          new GuiColumn().exec(g -> {
              g.setWidth(INFINITY);
          }).addChildren(
            new GuiRow().exec(g -> {
                g.addLayoutorAlignParentRR(NaN, NaN, 1f, NaN);
                g.addOnDrawListener(e -> drawRect(Colors.BLACK40, g));
            }).addChildren(
              new GuiButton("ESC").exec(g -> {
                  g.setWidth(42);
                  g.setHeight(20);
                  g.addOnClickListener(e -> Outskirts.getRootGUI().removeGui(this));
              }),
              new GuiRow().exec(g -> {
                  g.addLayoutorAlignParentLTRB(NaN, 0, 0, NaN);
              }).addChildren(
                new GuiButton("Settings").exec(g -> {
                    g.setWidth(20);
                    g.setHeight(20);
                    g.addOnClickListener(e -> Outskirts.getRootGUI().addGui(new GuiScreenOptions()));
                }),
                new Gui(0, 0, 10, 0),
                new GuiButton("EXIT").exec(g -> {
                    g.setWidth(20);
                    g.setHeight(20);
                    g.addOnClickListener(e -> {
                        Outskirts.setWorld(null);
//                      Outskirts.getPlayer().connection.closeChannel("Dinsconne");  //client ext.test
                        Outskirts.getRootGUI().removeAllGuis();
                        Outskirts.getRootGUI().addGui(GuiScreenMainMenu.INSTANCE);
                    });
                })
              )
            ),
            new GuiRow().exec(g -> {
                g.addLayoutorAlignParentRR(NaN, NaN, 1f, NaN);
                g.addOnDrawListener(e -> drawRect(Colors.BLACK80, g));
                g.setHeight(52);
            }).addChildren(
                    new GuiRow().exec(g -> {
                        g.addLayoutorAlignParentRR(.5f, .5f);
                    }).addChildren(
                            new GuiText("Homebase"),
                            new GuiText("Map"),
                            new GuiText("Contacts"),
                            new GuiText("Backpack"),
                            new GuiText("Skills"),
                            new GuiText("Tasks"),
                            new GuiText("Achievements"),  //Journal
                            new GuiText("Statites"),
                            new GuiText("Mails"),
                            new GuiText("Person")
                    )
            )
          )
        );
    }

    private static void drawPoint(Vector4f color, float x, float y) {
        drawRect(color, (int)(Outskirts.getWidth()/2 + x*50f), (int)(Outskirts.getHeight()/2 - y*50f), 2, 2);
    }

}
