package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.inspection.GuiIEntity;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.render.isoalgorithm.dc.HermiteData;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.renderer.post.PostRenderer;
import outskirts.entity.Entity;
import outskirts.entity.EntityStaticMesh;
import outskirts.entity.player.GameMode;
import outskirts.event.EventPriority;
import outskirts.init.ex.Models;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.Colors;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;
import outskirts.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;
import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec4;
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
                        new GuiExpander("DebugV").setContent(new GuiColumn().addChildren(
                                new GuiCheckBox("Memory Log").exec(g->initCBL(g, true, memLog::setVisible)),
                                new GuiCheckBox("Profiler Visual").exec(g->initCBL(g, true, profv::setVisible))
                        )),
                        new GuiText("GEO Rd. RENDERING."),
                        new GuiCheckBox("BasisVisual").exec(g->initCBL(g, true, basisv::setVisible)),
                        new GuiCheckBox("GBuff/v").exec(g->initCBL(g, false, gbufv::setVisible)),
                        new GuiCheckBox("FE/NormV").exec(g->{
                            g.addOnDrawListener(e -> renderDVGIfFEOkRGCk(Colors.YELLOW, Vector4f.ZERO, g));
                        }),
                        new GuiCheckBox("FE/BordV").exec(g->{
                            g.addOnDrawListener(e -> renderDVGIfFEOkRGCk(Vector4f.ZERO, Colors.DARK_GRAY, g));
                        }),
                        new GuiCheckBox("FRO/Octrees-P").exec((GuiCheckBox g)->{
                            g.addOnDrawListener(e -> {
                                Vector3f p = Outskirts.getRayPicker().getCurrentPoint();
                                if (g.isChecked() && p != null) {
                                    Octree nd = Outskirts.getWorld().getOctree(p);
                                    Vector3f base = Vector3f.floor(vec3(p), 16f);
                                    drawOctreeOp(nd, base, 16, vec3(p).sub(base).scale(1/16f));
                                }
                            });
                        }),
                        new GuiCheckBox("FRO/OctreesAHD").exec((GuiCheckBox g)->{
                            g.addOnDrawListener(e -> {
                                Vector3f base = Vector3f.floor(vec3(Outskirts.getPlayer().position()).setY(0), 16f);
                                Octree nd = Outskirts.getWorld().getOctree(base);
                                if (g.isChecked() && nd != null) {
                                    drawOctreeAHD(nd, base, 16);
                                }
                            });
                        }),
                        new GuiCheckBox("Polymode: LINE. r. FILL").exec(g -> initCBL(g, false,  b->glPolygonMode(GL_FRONT_AND_BACK, b?GL_LINE: GL_FILL))),

                        new GuiExpander("COMM").setContent(new GuiColumn().addChildren(
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
                                })
                        )),
                        new GuiText("PHYS"),
                        new GuiCheckBox("BoundingBox").exec(g->initCBL(g, false, b-> GuiDebugPhys.INSTANCE.showBoundingBox=b)),
                        new GuiCheckBox("Velocities").exec(g->initCBL(g, false, b-> GuiDebugPhys.INSTANCE.showVelocities=b)),
                        new GuiCheckBox("ContactPoints").exec(g->initCBL(g, false, b-> GuiDebugPhys.INSTANCE.showContactPoints=b)),


                        new GuiExpander("INSP").setContent(new GuiColumn().addChildren(
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
                                new GuiCheckBox("Lights Marks").exec(g->initCBL(g, false, GuiILightsList.INSTANCE::setVisible))
                        )),
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

    private static Vector4f COL_INTERN = vec4(Colors.GRAY);
    private static Vector4f COL_LEAF_DIFF = vec4(Colors.RED);
    private static Vector4f COL_LEAF_FULL = vec4(Colors.DARK_RED).scale(0.75f);

    private static void drawOctreeAHD(Octree node, Vector3f min, float size) {
        Octree.forEach(node, (nd, mn, sz) -> {
            if (nd.isInternal()) {
                Outskirts.renderEngine.getModelRenderer().drawOutline(new AABB(vec3(mn), vec3(mn).add(sz)), COL_INTERN);
            } else {
                Outskirts.renderEngine.getModelRenderer().drawOutline(new AABB(vec3(mn), vec3(mn).add(sz)), ((Octree.Leaf)nd).vfull() ? COL_LEAF_FULL : COL_LEAF_DIFF);
                drawOctreeLeafEdgesHDT((Octree.Leaf)nd, mn);
            }
        }, min, size);
    }

    private static void drawOctreeOp(Octree node, Vector3f min, float sz, Vector3f rp) {
        if (node.isInternal()) {
            Outskirts.renderEngine.getModelRenderer().drawOutline(new AABB(min, vec3(min).add(sz,sz,sz)), COL_INTERN);
            int idx = Octree.cellidx(rp);
            Octree sub = ((Octree.Internal)node).child(idx);
            Octree.cellrpclip(idx, rp);
            drawOctreeOp(sub, vec3(min).addScaled(sz/2f, Octree.VERT[idx]), sz/2f, rp);
        } else {
            Octree.Leaf leaf = (Octree.Leaf)node;
            Outskirts.renderEngine.getModelRenderer().drawOutline(new AABB(min, vec3(min).add(sz,sz,sz)), COL_LEAF_DIFF);
            drawOctreeLeafEdgesHDT(leaf, min);
            Gui.drawWorldpoint(min, (x, y) -> Gui.drawString("S"+leaf.size, x, y, Colors.GRAY, 8));
        }
    }

    private static void drawOctreeLeafEdgesHDT(Octree.Leaf leaf, Vector3f min) {
        for (int i=0;i<12;i++) {
            HermiteData h = leaf.edges[i];
            if (h != null) {
                Vector3f base = vec3(h.point).add(Vector3f.floor(vec3(min),16f));
                Outskirts.renderEngine.getModelRenderer().drawLine(base, vec3(base).addScaled(.2f,h.norm), Colors.GREEN);
            }
        }
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
