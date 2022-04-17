package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiAlign;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.compoents.GuiMap;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.event.EventPriority;
import outskirts.util.Colors;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;

public class GuiScreenPause extends Gui {

    public static GuiScreenPause INSTANCE = new GuiScreenPause();

    private Gui gDisplay;

    public GuiScreenPause() {
        setWidth(INF);
        setHeight(INF);

        initEscClose(this);

        build();
    }

    private void build() {

        gDisplay=new Gui().exec(g -> {
//            g.addLayoutorAlignParentLTRB(0, 72+5, 0, 5);
            g.addOnDrawListener(e -> {
                drawRect(Colors.BLACK40, g);
            });
        });

        addChildren(
          new GuiColumn().exec(g -> {
              g.setWidth(INF);
              g.setHeight(INF);
          }).addChildren(
            new Gui().exec(g -> {  // ROW: TopBar
                g.addOnDrawListener(e -> drawRect(Colors.BLACK, g));
                g.setWidth(INF);
                g.setHeight(20);
            }).addChildren(
              new GuiButton("ESC").exec(g -> {
                  ((GuiButton)g)._drawBackground = false;
                  g.setWidth(50);
                  g.setHeight(20);
                  g.addOnClickListener(e -> Outskirts.getRootGUI().removeGui(this));
              }),
              new GuiRow().exec(g -> {
                  g.addLayoutorAlignParentLTRB(NaN, 0, 0, NaN);
              }).addChildren(
                new GuiButton("Settings").exec(g -> {
                    g.setHeight(20);
                    g.setWidth(60);
                    ((GuiButton)g)._drawBackground = false;
                    g.addOnClickListener(e -> {
                        displayContentGUI(new GuiScreenOptions.GuiGraphicOptions());
                    });
                }),
                new Gui(0, 0, 10, 0),
                new GuiButton("Exit").exec(g -> {
                    g.setHeight(20);
                    g.setWidth(50);
                    ((GuiButton)g)._drawBackground = false;
                    g.addOnClickListener(e -> {
                        Outskirts.setWorld(null);
//                      Outskirts.getPlayer().connection.closeChannel("Dinsconne");  //client ext.test
                        Outskirts.getRootGUI().removeAllGuis();
                        Outskirts.getRootGUI().addGui(GuiScreenMainMenu.INSTANCE);
                    });
                })
              )
            ),
            new Gui().exec(g -> {
                g.setWidth(INF);
                g.setHeight(38);
                g.addOnDrawListener(e -> {
                    drawRect(Colors.BLACK80, g);
                });
            }).addChildren(
              new GuiAlign(.082f, .5f).addChildren(
                  new GuiRow().addChildren(
//                  gTab("Homebase", new GuiText("HMBS")).exec(g -> g.performEvent(new Gui.OnPressedEvent())),
                          gTab("Map", new GuiMap().exec(g -> {
                              g.setWidth(INF);
                              g.setHeight(INF);
                          })),
//                  gTab("Contacts", new GuiText("CTT")),
                          gTab("Inventory", new GuiText("BP")),
//                  gTab("Skills", new GuiText("SK")),
//                  gTab("Tasks", new GuiText("TS")),
//                  gTab("Achievements", new GuiText("AC")),  //Journal
                          gTab("Statitics", new GuiText("ST"))
//                  gTab("Mails", new GuiText("ML")),
//                  gTab("Person", new GuiText("PS"))
                  )
              ),
              new GuiAlign(0.918f, 0.5f).addChildren(
                  new GuiText(Outskirts.getPlayer().getName()).exec((GuiText g) -> {
                      g.setTextHeight(24);
                  })
              )
            ),
            new GuiAlign().useLTRB(0, 10, 0, 4).setContent(gDisplay)
          )
        );


    }

    private Gui currTab;
    private Gui gTab(String name, Gui gd) {
        return new GuiText(name+"       ").exec((GuiText g) -> {
            g.addOnPressedListener(e -> {
                displayContentGUI(gd);
                if (currTab!=null)  // prevents first auto init-select sound
                    GuiButton.playClickSound();
                currTab=g;
            });
            g.addOnDrawListener(e -> {
                g.getTextColor().set(currTab==g?Colors.WHITE: (g.isHover() ? Colors.YELLOW:Colors.GRAY));
            }).priority(EventPriority.HIGH);
        });
    }

    private void displayContentGUI(Gui gd) {
        gDisplay.removeAllGuis();
        gDisplay.addGui(gd);
    }

    private static void drawPoint(Vector4f color, float x, float y) {
        drawRect(color, (int)(Outskirts.getWidth()/2 + x*50f), (int)(Outskirts.getHeight()/2 - y*50f), 2, 2);
    }

}
