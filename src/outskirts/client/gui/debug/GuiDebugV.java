package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.inspection.GuiIEntity;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.render.renderer.post.PostRenderer;
import outskirts.entity.Entity;
import outskirts.entity.EntityStaticMesh;
import outskirts.entity.player.GameMode;
import outskirts.event.EventPriority;
import outskirts.init.ex.Models;
import outskirts.util.Colors;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector4f;
import outskirts.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static outskirts.util.logging.Log.LOGGER;

public class GuiDebugV extends Gui {

    public static GuiDebugV INSTANCE = new GuiDebugV();

    public GuiDebugV() {
        setWidth(INFINITY);
        setHeight(INFINITY);

        GuiMemoryLog memLog;
        GuiProfilerVisual profv;
        GuiBasisVisual basisv;
        GuiEntityGBufferVisual gbufv;

        Matrix3f theBasis = new Matrix3f();
        addChildren(
                GuiDebugTxInfos.INSTANCE,
                GuiDebugPhys.INSTANCE,
                GuiILightsList.INSTANCE,
                basisv=new GuiBasisVisual(theBasis, true).exec((GuiBasisVisual g) -> {
                    g.addOnDrawListener(e -> Matrix3f.set(theBasis, Outskirts.renderEngine.getViewMatrix())).priority(EventPriority.HIGH);
                    g.addLayoutorAlignParentRR(0.5f, 0.5f);
                }),
                memLog=new GuiMemoryLog().exec(g -> {
                    g.addLayoutorAlignParentLTRB(0, NaN, NaN, 0);
                }),
                profv=new GuiProfilerVisual().exec(g -> {
                    g.addLayoutorAlignParentLTRB(NaN, NaN, 10, 10);
                }),
                gbufv=new GuiEntityGBufferVisual(),
                new GuiColumn().exec(g -> {
                    g.addLayoutorAlignParentLTRB(NaN, 30, 30, NaN);
                    g.addOnDrawListener(e -> {
                        drawRect(Colors.BLACK80, g);
                    });
                }).addChildren(
                        new GuiText("DebugV"),
                        new GuiCheckBox("Memory Log").exec(g->initCBL(g, true, memLog::setVisible)),
                        new GuiCheckBox("Profiler Visual").exec(g->initCBL(g, true, profv::setVisible)),
                        new GuiText("GEO Rd. RENDERING."),
                        new GuiCheckBox("BasisVisual").exec(g->initCBL(g, true, basisv::setVisible)),
                        new GuiCheckBox("GBuff/v").exec(g->initCBL(g, false, gbufv::setVisible)),
                        new GuiCheckBox("FE/NormV").exec(g->{
                            g.addOnDrawListener(e -> renderDVGIfFEOkRGCk(Colors.YELLOW, Vector4f.ZERO, g));
                        }),
                        new GuiCheckBox("FE/BordV").exec(g->{
                            g.addOnDrawListener(e -> renderDVGIfFEOkRGCk(Vector4f.ZERO, Colors.DARK_GRAY, g));
                        }),
                        new GuiCheckBox("Polymode: LINE. r. FILL").exec(g -> initCBL(g, false,  b->glPolygonMode(GL_FRONT_AND_BACK, b?GL_LINE: GL_FILL))),
                        new GuiText("COMM"),
                        new GuiComboBox().exec((GuiComboBox g) -> {
                            g.getOptions().addAll(List.of(
                                    new GuiText("SURVIVAL"),
                                    new GuiText("CREATIVE"),
                                    new GuiText("SPECTATOR")
                            ));
                            g.addOnSelectedListener(e -> {
                                Outskirts.getPlayer().gamemode = GameMode.values()[g.getSelectedIndex()];
                            });
                            g.setSelectedIndex(Outskirts.getPlayer().gamemode.ordinal());
                        }),
                        new GuiText("PHYS"),
                        new GuiCheckBox("BoundingBox").exec(g->initCBL(g, false, b-> GuiDebugPhys.INSTANCE.showBoundingBox=b)),
                        new GuiCheckBox("Velocities").exec(g->initCBL(g, false, b-> GuiDebugPhys.INSTANCE.showVelocities=b)),
                        new GuiCheckBox("ContactPoints").exec(g->initCBL(g, false, b-> GuiDebugPhys.INSTANCE.showContactPoints=b)),
                        new GuiText("INSP"),
                        new GuiButton("GIPlayer").exec(g -> {
                            g.addOnClickListener(e -> Outskirts.getRootGUI().addGui(new GuiWindow(new GuiIEntity(Outskirts.getPlayer()))));
                        }),
                        new GuiButton("GIPickerETT").exec(g -> {
                            g.addOnClickListener(e -> {
                                if (Outskirts.getRayPicker().getCurrentEntity() == null) return;
                                Outskirts.getRootGUI().addGui(new GuiWindow(new GuiIEntity(Outskirts.getRayPicker().getCurrentEntity())));
                            });
                        }),
                        new GuiButton("Add EStaticMesh.").exec(g -> {
                            g.addOnClickListener(e -> {
                                if (Outskirts.getRayPicker().getCurrentPoint() == null) return;
                                EntityStaticMesh sm = new EntityStaticMesh();
                                sm.setModel(Models.GEO_CUBE);
                                sm.rigidbody().transform().origin.set(Outskirts.getRayPicker().getCurrentPoint());
                                Outskirts.getWorld().addEntity(sm);
                            });
                        }),
                        new GuiCheckBox("Lights Marks").exec(g->initCBL(g, false, GuiILightsList.INSTANCE::setVisible)),
                        new GuiText("TEST"),
                        new GuiButton("Gui Weights Test Window").exec(g -> {
                            g.setWidth(NaN);
                            g.addOnClickListener(e -> Outskirts.getRootGUI().addGui(new GuiWindow(new GuiTestWindowWidgets())));
                        }),
                        new GuiSlider().exec((GuiSlider g) -> {
                            g.setUserMinMaxValue(0.01f, 10);
                            g.addOnValueChangedListener(ge -> {
                                PostRenderer.exposure = g.getCurrentUserValue();
                                LOGGER.info(PostRenderer.exposure);
                            });
                        }),
                        new GuiSlider().exec((GuiSlider g) -> {
                            g.setUserMinMaxValue(0, 10);
                            g.addOnValueChangedListener(ge -> {
                                if (g.getCurrentUserValue() != World.sz) {
                                    World.sz = (int)g.getCurrentUserValue();
                                    g.setCurrentUserValue(World.sz);
                                    LOGGER.info(World.sz);
                                }
                            });
                        })
                )
        );
    }

    private static void renderDVGIfFEOkRGCk(Vector4f ncol, Vector4f bcol, Gui g) {
        if (!((Checkable)g).isChecked())
            return;
        Entity entity = Outskirts.getRayPicker().getCurrentEntity();
        if (entity != null) {
            Outskirts.renderEngine.getDebugVisualGeoRenderer().normColor.set(ncol);
            Outskirts.renderEngine.getDebugVisualGeoRenderer().borderColor.set(bcol);
            Outskirts.renderEngine.getDebugVisualGeoRenderer().render(entity);
        }
    }

    private static void initCBL(Gui g, boolean dfb, Consumer<Boolean> onchecked) {
        GuiCheckBox cb = (GuiCheckBox)g;
        cb.addOnCheckedListener(e -> {
            onchecked.accept(cb.isChecked());
        });
        cb.setChecked(dfb);
    }
}
