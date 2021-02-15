package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.compoents.GuiMap;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.event.EventPriority;
import outskirts.event.Events;
import outskirts.event.client.input.KeyboardEvent;
import outskirts.util.Colors;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;

import static java.lang.Float.NaN;
import static java.lang.Float.POSITIVE_INFINITY;

public class GuiScreenPause extends Gui {

    public static GuiScreenPause INSTANCE = new GuiScreenPause();

    private Gui gDisplay;

    public GuiScreenPause() {
        setWidth(INFINITY);
        setHeight(INFINITY);

        initEscClose(this);

        build();
    }

    private void build() {

        gDisplay=new Gui().exec(g -> {
            g.addLayoutorAlignParentLTRB(0, 72+5, 0, 5);
            g.addOnDrawListener(e -> {
                drawRect(Colors.BLACK40, g);
            });
        });

        addChildren(
          new GuiColumn().exec(g -> {
              g.setWidth(INFINITY);
              g.setHeight(INFINITY);
          }).addChildren(
            new GuiRow().exec(g -> {
                g.addLayoutorAlignParentRR(NaN, NaN, 1f, NaN);
                g.addOnDrawListener(e -> drawRect(Colors.BLACK, g));
                g.setHeight(20);
            }).addChildren(
              new GuiButton("ESC").exec(g -> {
                  g.setWidth(42);
                  g.setHeight(20);
                  g.addOnClickListener(e -> Outskirts.getRootGUI().removeGui(this));
              }),
              new GuiRow().exec(g -> {
                  g.addLayoutorAlignParentLTRB(NaN, 0, 0, NaN);
              }).addChildren(
                new GuiButton("S").exec(g -> {
                    g.setWidth(20);
                    g.setHeight(20);
                    g.addOnClickListener(e -> Outskirts.getRootGUI().addGui(new GuiScreenOptions()));
                }),
                new Gui(0, 0, 10, 0),
                new GuiButton("E").exec(g -> {
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
                  g.addLayoutorAlignParentRR(.082f, .5f);
              }).addChildren(
                gTab("Homebase", new GuiText("HMBS")).exec(g -> g.performEvent(new Gui.OnPressedEvent())),
                gTab("Map", new GuiMap().exec(g -> {
                    g.setWidth(INFINITY);
                    g.setHeight(INFINITY);
                })),
                gTab("Contacts", new GuiText("CTT")),
                gTab("Backpack", new GuiText("BP")),
                gTab("Skills", new GuiText("SK")),
                gTab("Tasks", new GuiText("TS")),
                gTab("Achievements", new GuiText("AC")),  //Journal
                gTab("Statites", new GuiText("ST")),
                gTab("Mails", new GuiText("ML")),
                gTab("Person", new GuiText("PS"))
              )
//              new GuiText(Outskirts.getPlayer().getName()).exec((GuiText g) -> {
//                  g.addLayoutorAlignParentLTRB(NaN, 17-8, 80, NaN);
//                  g.setTextHeight(18);
//              })
            ),
            gDisplay
          )
        );


    }

    private Gui currTab;
    private Gui gTab(String name, Gui gd) {
        return new GuiText(name+"    ").exec((GuiText g) -> {
            g.addOnPressedListener(e -> {
                gDisplay.removeAllGuis();
                gDisplay.addGui(gd);
                if (currTab!=null)  // prevents first auto init-select sound
                    GuiButton.playClickSound();
                currTab=g;
            });
            g.addOnDrawListener(e -> {
                g.getTextColor().set(currTab==g?Colors.WHITE: (g.isHover() ? Colors.YELLOW:Colors.GRAY));
            }).priority(EventPriority.HIGH);
        });
    }

    private static void drawPoint(Vector4f color, float x, float y) {
        drawRect(color, (int)(Outskirts.getWidth()/2 + x*50f), (int)(Outskirts.getHeight()/2 - y*50f), 2, 2);
    }

}
