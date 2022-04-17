package outskirts.client.gui.screen;

import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.client.render.renderer.RenderEngine;
import outskirts.client.render.renderer.post.PostRenderer;
import outskirts.util.Colors;
import outskirts.world.World;

import java.util.Arrays;

public class GuiScreenOptions extends Gui {

    public static final GuiScreenOptions INSTANCE = new GuiScreenOptions();

    private GuiScrollPanel gOptionsDisplay;

    public GuiScreenOptions() {
        setWidth(INF);
        setHeight(INF);
        addOnDrawListener(e -> {
            drawRect(Colors.BLACK10, this);
        });
        initEscClose(this);

        addChildren(
          new GuiScrollPanel().exec((GuiScrollPanel g) -> {
              g.addLayoutorAlignParentLTRB(72, 20, NaN, 0);
              g.addOnDrawListener(e -> {
                  drawRect(Colors.BLACK80, g);
              });
          }).setContent(
              new GuiColumn().exec(g -> {

              }).addChildren(
                new GuiButton("Video/ Graphi").exec(g -> {
                    g.addOnClickListener(e -> gOptionsDisplay.setContent(new GuiGraphicOptions()));
                }),
                new GuiButton("Music/ Soundx"),
                new GuiButton("Controls"),
                new GuiButton("Language"),
                new GuiButton("Resource Packs"),
                new GuiButton("Mods")
              ).exec(g -> {
                  g.getChildren().forEach(c -> {
                      c.setWidth(160);
                      c.setHeight(40);
                  });
              })
          ),
          gOptionsDisplay=new GuiScrollPanel().exec((GuiScrollPanel g) -> {
              g.addLayoutorAlignParentLTRB(255, 20, 42, 20);
              g.addOnDrawListener(e -> {
                  drawRect(Colors.BLACK80, g);
              });
          })
        );
    }

    static class GuiGraphicOptions extends Gui {

        public GuiGraphicOptions() {

            addChildren(
              new GuiColumn().exec(g -> {
                  g.setRelativeXY(20, 20);
              }).addChildren(
                new GuiText("Vd Ops").exec((GuiText g) -> {
                    g.setTextHeight(28);
                }),
                row("FOV", new GuiSlider().exec((GuiSlider g) -> {
                    g.setUserMinMaxValue(10, 140);
                    g.getUserOptionalValues().addAll(Arrays.asList(30f, 70f, 90f));
                    g.initOnlyIntegerValues();
                    g.initValueSync(
                            () -> Outskirts.renderEngine.getFov(),
                            f -> Outskirts.renderEngine.setFov(f));

                    g.setForceUserOptionalValues(true);
                })),
                row("RenderDistance", new GuiSlider().exec((GuiSlider g) -> {
                    g.setUserMinMaxValue(0, 10);
                    g.getUserOptionalValues().addAll(Arrays.asList(0f, 1f, 2f, 3f, 6f, 8f));
                    g.initOnlyIntegerValues();
                    g.initValueSync(
                            () -> (float)World.sz,
                            f -> World.sz = f.intValue());
                })),
                row("VSync", new GuiSwitch().exec((GuiSwitch g) -> {
//                    g.initCheckedSync(
//                            () -> Outskirts.getWindow().isVSync(),
//                            b -> Outskirts.getWindow().setVSync(b));

                })),
                row("FPS Capacity", new GuiSlider().exec((GuiSlider g) -> {
                    g.setUserMinMaxValue(1, 200);
                    g.getUserOptionalValues().addAll(Arrays.asList(30f, 60f, 120f, 144f));
                    g.initOnlyIntegerValues();
                    g.initValueSync(
                            () -> (float)ClientSettings.FPS_CAPACITY,
                            f -> ClientSettings.FPS_CAPACITY=f.intValue());
                })),
                row("GUI Scale", new GuiSlider().exec((GuiSlider g) -> {
                    g.setUserMinMaxValue(0.5f, 2.5f);
                    g.getUserOptionalValues().addAll(Arrays.asList(.5f, 1f, 1.5f, 2f));
                    g.initValueSync(
                            () -> ClientSettings.GUI_SCALE,
                            f -> ClientSettings.GUI_SCALE=f);
                    g.setForceUserOptionalValues(true);
                })),
                row("Fog Density", new GuiSlider().exec((GuiSlider g) -> {
                    g.setUserMinMaxValue(0.0001f, 0.1f);
                    g.initValueSync(
                            () -> Outskirts.renderEngine.getEntityRenderer().fogDensity,
                            f -> Outskirts.renderEngine.getEntityRenderer().fogDensity=f);
                })),
                row("Fog Gradient", new GuiSlider().exec((GuiSlider g) -> {
                    g.setUserMinMaxValue(0, 4);
                    g.initValueSync(
                            () -> Outskirts.renderEngine.getEntityRenderer().fogGradient,
                            f -> Outskirts.renderEngine.getEntityRenderer().fogGradient=f);
                })),
                row("Sky Height", new GuiSlider().exec((GuiSlider g) -> {
                    g.setUserMinMaxValue(0, 1000);
                    g.initValueSync(
                            () -> RenderEngine.skyHei,
                            f -> RenderEngine.skyHei=f);
                })),
                row("Post Exposure", new GuiSlider().exec((GuiSlider g) -> {
                    g.setUserMinMaxValue(0.01f, 10);
                    g.initValueSync(
                            () -> PostRenderer.exposure,
                            f -> PostRenderer.exposure=f);
                }))
              )
            );

        }
    }

    private static Gui row(String title, Gui op) {
        return new GuiRow().exec(g -> {
            g.setHeight(46);
        }).addChildren(
                new GuiText(title).exec(g -> {
                    g.setWidth(150);
                    g.setRelativeX(5);  // need vertical center?
                }),
                op
        );
    }

//    private GuiButton btnDone = addGui(new GuiButton("Done")); {
//        btnDone.addOnClickListener(e -> {
//            Outskirts.getRootGUI().removeGui(this);
//        });
//        btnDone.addLayoutorAlignParentRR(0.5f, NaN);
//        btnDone.addLayoutorAlignParentLTRB(NaN, NaN, NaN, 80);
//    }
//
//    private GuiSlider slFov = addGui(new GuiSlider()); {
//        slFov.addOnValueChangedListener(e -> {
//            ClientSettings.FOV = (int)slFov.getCurrentUserValue();
//
////            slider.setText("FOV: " + ClientSettings.FOV);
//        });
//        slFov.setUserMinValue(25);
//        slFov.setUserMaxValue(115);
//        slFov.setCurrentUserValue(ClientSettings.FOV);
//        slFov.addOnDrawListener(e -> {
//            slFov.addLayoutorAlignParentRR(0.5f, NaN);
//            slFov.setY(80);
//        });
//    }
//
//    private GuiButton btnMusicAndSounds = addGui(new GuiButton("Music & Sounds...")); {
//        btnMusicAndSounds.addLayoutorAlignParentRR(0.5f, NaN);
//        btnMusicAndSounds.setY(140);
//    }
//
//    private GuiButton btnVideoSettings = addGui(new GuiButton("Video Settings...")); {
//        btnVideoSettings.addLayoutorAlignParentRR(0.5f, NaN);
//        btnVideoSettings.setY(200);
//    }
//
//    private GuiSlider slGuiscale = addGui(new GuiSlider()); {
//        slGuiscale.addOnValueChangedListener(e -> {
//            ClientSettings.GUI_SCALE = Maths.floor(slGuiscale.getCurrentUserValue(), 0.1f);
////            slGuiscale.setText("GUI_SCALE: " + ClientSettings.GUI_SCALE);
//        });
//        slGuiscale.setUserMinValue(0.5f);
//        slGuiscale.setUserMaxValue(4);
//        slGuiscale.setCurrentUserValue(ClientSettings.GUI_SCALE);
//        slGuiscale.addLayoutorAlignParentRR(0.5f, NaN);
//        slGuiscale.setY(370);
//    }

//    private GuiSlider slWfRenderQuality = addGui(new GuiSlider()); {
//        slWfRenderQuality.setUserMinMaxValue(0.01f, 1.0f);
//        slWfRenderQuality.setCurrentUserValue(Outskirts.renderEngine.RENDERE_QUALITY);
//        slWfRenderQuality.addOnValueChangedListener(e -> {
//            Outskirts.renderEngine.RENDERE_QUALITY = slWfRenderQuality.getCurrentUserValue();
//            Outskirts.renderEngine.updateRenderQuality();
//        });
//        slGuiscale.addLayoutorAlignParentRR(0.5f, NaN);
//        slGuiscale.setY(400);
//    }

}
