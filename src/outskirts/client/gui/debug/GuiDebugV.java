package outskirts.client.gui.debug;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.inspection.GuiIEntity;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.render.chunk.RenderSection;
import outskirts.client.render.isoalgorithm.dc.HermiteData;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.renderer.RenderEngine;
import outskirts.entity.Entity;
import outskirts.entity.item.EntityStaticMesh;
import outskirts.entity.player.Gamemode;
import outskirts.event.EventPriority;
import outskirts.init.ex.Models;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.Colors;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.*;

public class GuiDebugV extends Gui {

    public static GuiDebugV INSTANCE = new GuiDebugV();

    public GuiDebugV() {
        setWidth(INF);
        setHeight(INF);

        GuiMemoryLog memLog;
        GuiProfilerVisual profv;
        GuiBasisVisual basisv;
        GuiEntityGBufferVisual gbufv;

        addKeyboardListener(e -> {
            if (e.isPressed() && e.getKey() == GLFW.GLFW_KEY_R) {
                requestLayout();
            }
        });

        Matrix3f theBasis = new Matrix3f();
        addChildren(
                GuiDebugTxInfos.INSTANCE,
                GuiDebugPhys.INSTANCE,
                GuiILightsList.INSTANCE,
                new GuiAlign(.5f, .5f).setContent(
                    basisv=new GuiBasisVisual(theBasis, true).exec((GuiBasisVisual g) -> {
                        // update mat before draw.
                        g.addOnDrawListener(e -> Matrix3f.set(theBasis, Outskirts.renderEngine.getViewMatrix())).priority(EventPriority.HIGH);
                    })
                ),
                new GuiAlign(0, 1).setContent(memLog=new GuiMemoryLog()),
                new GuiAlign(.98f, .98f).setContent(profv=new GuiProfilerVisual()),  // 10px original.x
                gbufv=new GuiEntityGBufferVisual(),
                new GuiPopupMenu.GuiMenubar().exec((GuiPopupMenu.GuiMenubar menubar) -> {

                    menubar.addMenu("Profiler", new GuiPopupMenu().exec((GuiPopupMenu menu) -> {
                        menu.addItem(new GuiColumn().addChildren(
                                new GuiCheckBox("Memory Log").exec(g->initCBL(g, true, memLog::setVisible)),
                                new GuiCheckBox("Profiler Visual").exec(g->initCBL(g, true, profv::setVisible))
                        ));
                    }));

                    menubar.addMenu("Physics", new GuiPopupMenu().exec((GuiPopupMenu menu) -> {
                        menu.addItem(new GuiColumn().addChildren(
                                new GuiCheckBox("BoundingBox").exec(g->initCBL(g, false, b-> GuiDebugPhys.INSTANCE.showBoundingBox=b)),
                                new GuiCheckBox("Velocities").exec(g->initCBL(g, false, b-> GuiDebugPhys.INSTANCE.showVelocities=b)),
                                new GuiCheckBox("ContactPoints").exec(g->initCBL(g, false, b-> GuiDebugPhys.INSTANCE.showContactPoints=b))
                        ));
                    }));

                    menubar.addMenu("GEO Rd. RENDERING.", new GuiPopupMenu().exec((GuiPopupMenu menu) -> {
                        menu.addItem(new GuiColumn().addChildren(
                                new GuiCheckBox("BasisVisual").exec(g->initCBL(g, true, basisv::setVisible)),
                                new GuiCheckBox("GBuff/v").exec(g->initCBL(g, false, gbufv::setVisible)),
                                new GuiCheckBox("FE/NormV").exec(g->{
                                    addOnDrawListener(e -> renderDVGIfFEOkRGCk(Colors.YELLOW, Vector4f.ZERO, g));
                                }),
                                new GuiCheckBox("FE/BorderV").exec((GuiCheckBox g)->{
                                    g.setChecked(true);
                                    addOnDrawListener(e -> renderDVGIfFEOkRGCk(Vector4f.ZERO, Colors.BLACK80, g));
                                }),
                                new GuiCheckBox("FrustumCulling").exec((GuiCheckBox g)->{
                                    g.setChecked(true);
                                    g.initCheckedSync(
                                            () -> RenderEngine.dbg_EnableFrustumUpdate,
                                            b -> RenderEngine.dbg_EnableFrustumUpdate=b);
                                }),
                                new GuiCheckBox("Polymode: LINE. r. FILL").exec(g -> initCBL(g, false,  b->glPolygonMode(GL_FRONT_AND_BACK, b?GL_LINE: GL_FILL)))
                        ));
                    }));

                    menubar.addMenu("WorldC", new GuiPopupMenu().exec((GuiPopupMenu menu) -> {
                        menu.addItem(new GuiColumn().addChildren(
                                new GuiCheckBox("FRO/Octrees-P").exec((GuiCheckBox g)->{
                                    addOnDrawListener(e -> {
                                        Vector3f p = Outskirts.getRayPicker().getCurrentPoint();
                                        if (g.isChecked() && p != null) {
                                            Octree nd = Outskirts.getWorld().getOctree(p);
                                            Vector3f base = Vector3f.floor(vec3(p), 16f);
                                            drawOctreeOp(nd, base, 16, vec3(p).sub(base).scale(1/16f));
                                        }
                                    });
                                }),
                                new GuiCheckBox("FRO/OctreesAHD").exec((GuiCheckBox g)->{
                                    addOnDrawListener(e -> {
                                        Vector3f base = Vector3f.floor(vec3(Outskirts.getPlayer().position()), 16f);
                                        Octree nd = Outskirts.getWorld().getOctree(base);
                                        if (g.isChecked() && nd != null) {
                                            drawOctreeAHD(nd, base, 16);
                                        }
                                        Vector3f p = Outskirts.getRayPicker().getCurrentPoint();
                                        if (p != null && Outskirts.isCtrlKeyDown()) {
                                            Vector3f pbase = Vector3f.floor(vec3(p), 16f);
                                            Octree pnd = Outskirts.getWorld().getOctree(pbase);
                                            if (g.isChecked() && pnd != null) {
                                                drawOctreeAHD(pnd, pbase, 16);
                                            }
                                        }
                                    });
                                }),
                                new GuiCheckBox("MarkedRenderSections.").exec((GuiCheckBox g)->{
                                    g.setChecked(true);
                                    addOnDrawListener(e -> {
                                        if (g.isChecked()) renderMarkedRenderSections();
                                    });
                                }),
                                new GuiCheckBox("SectionBoundary.").exec((GuiCheckBox g)->{
                                    addOnDrawListener(e -> {
                                        if (g.isChecked()) Outskirts.renderEngine.getModelRenderer().drawOutline(aabb(vec3floor(Outskirts.getPlayer().position(), 16f), 16), Colors.WHITE);
                                    });
                                }),
                                new GuiCheckBox("LODSizes.").exec((GuiCheckBox g)->{
                                    addOnDrawListener(e -> {
                                        if (g.isChecked()) renderLODSizes();
                                    });
                                })
                        ));
                    }));



                    menubar.addMenu("GGeneral", new GuiPopupMenu().exec((GuiPopupMenu menu) -> {
                        menu.addItem(new GuiColumn().addChildren(
                                new GuiComboBox().exec((GuiComboBox g) -> {
                                    Arrays.asList(Gamemode.values()).forEach(gm -> {
                                            g.getOptions().add(new GuiText(gm.name()));
                                    });
                                    g.initSelectionSync(
                                            () -> Outskirts.getPlayer().getGamemode().ordinal(),
                                            i -> Outskirts.getPlayer().setGamemode(Gamemode.values()[i]));
                                })
                        ));
                    }));



                    menubar.addMenu("Inspection", new GuiPopupMenu().exec((GuiPopupMenu menu) -> {
                        menu.addItem(new GuiColumn().addChildren(
                                new GuiButton("Insp Player").exec(g -> {
                                    g.addOnClickListener(e -> Outskirts.getRootGUI().addGui(new GuiWindow(new GuiIEntity(Outskirts.getPlayer()))));
                                }),
                                new GuiButton("Insp Picker Entity").exec(g -> {
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
                                        sm.getRigidBody().transform().origin.set(Outskirts.getRayPicker().getCurrentPoint());
                                        Outskirts.getWorld().addEntity(sm);
                                    });
                                }),
                                new GuiCheckBox("Lights Marks").exec(g->initCBL(g, false, GuiILightsList.INSTANCE::setVisible))
                        ));
                    }));



                    menubar.addMenu("GUI", new GuiPopupMenu().exec((GuiPopupMenu menu) -> {
                        menu.addItem(new GuiColumn().addChildren(
                                new GuiButton("Test W").exec(g -> {
                                    g.addOnClickListener(e -> Outskirts.getRootGUI().addGui(new GuiWindow(new GuiTestWindowWidgets())));
                                }),
                                new GuiButton("DumpRTG").exec(g -> {
                                    g.addOnClickListener(e -> {
                                        Outskirts.getRootGUI().addGui(new GuiWindow(new GuiDebugSnapshot(Outskirts.getRootGUI())));
                                    });
                                })
                        ));
                    }));
                })
        );
    }

    private static Vector4f COL_INTERN = vec4(Colors.GRAY);
    private static Vector4f COL_LEAF_DIFF = vec4(Colors.RED);
    private static Vector4f COL_LEAF_FULL = vec4(Colors.DARK_RED).scale(0.75f);

    private static void drawOctreeAHD(Octree node, Vector3f min, float size) {
        Octree.forEach(node, (nd, mn, sz) -> {
            if (nd.isInternal()) {
                if (vec3(Outskirts.getPlayer().position()).sub(mn).length() < 8f)
                Outskirts.renderEngine.getModelRenderer().drawOutline(new AABB(vec3(mn), vec3(mn).add(sz)), COL_INTERN);
            } else {
                if (vec3(mn).sub(Outskirts.getPlayer().position()).length() < 5f) {
                    Outskirts.renderEngine.getModelRenderer().drawOutline(new AABB(vec3(mn), vec3(mn).add(sz)), ((Octree.Leaf) nd).vfull() ? COL_LEAF_FULL : COL_LEAF_DIFF);
                    drawOctreeLeafEdgesHDT((Octree.Leaf) nd, mn);
                }
            }
        }, min, size);
    }

    // min: worldpos.
    private static void drawOctreeOp(Octree node, Vector3f min, float sz, Vector3f rp) {
        if (node==null)return;
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
            Gui.drawWorldpoint(min, (x, y) -> Gui.drawString("S"+leaf.size, x, y, Colors.GRAY, 16));
        }
    }

    // min: worldpos
    private static void drawOctreeLeafEdgesHDT(Octree.Leaf leaf, Vector3f min) {
        for (int i=0;i<12;i++) {
            HermiteData h = leaf.edges[i];
            if (h != null) {
                Vector3f base = vec3(h.point).add(Vector3f.floor(vec3(min),16f));
                Outskirts.renderEngine.getModelRenderer().drawLine(base, vec3(base).addScaled(.2f,h.norm), Colors.GREEN);
            } else {  // DEBUG Virtual-Leaf. (no-hermite-data, have signs and fp.
                if (leaf.signchange(i)) {
                    Vector3f p = Octree.edgelerp(leaf, i, .5f, null).sub(leaf.min).add(min);
                    Gui.drawWorldpoint(p, (x, y) -> {
                        drawRect(Colors.WHITE, x, y, 4, 4);
                    });
                }
            }
        }
    }

    private static void renderDVGIfFEOkRGCk(Vector4f ncol, Vector4f bcol, Gui g) {
        if (!((Checkable)g).isChecked())
            return;
        List<Entity> entities = new ArrayList<>();
        if (Outskirts.isCtrlKeyDown()) {
            entities.addAll(Outskirts.getWorld().getEntities());
        } else {
            Entity entity = Outskirts.getRayPicker().getCurrentEntity();
            if (entity != null)
                entities.add(entity);
        }
        for (Entity entity : entities) {
            Outskirts.renderEngine.getDebugVisualGeoRenderer().normColor.set(ncol);
            Outskirts.renderEngine.getDebugVisualGeoRenderer().borderColor.set(bcol);
            Outskirts.renderEngine.getDebugVisualGeoRenderer().render(entity);
        }
    }

    private static void renderMarkedRenderSections() {
        for (RenderSection rs : Outskirts.renderEngine.getChunkRenderDispatcher().getRenderSections()) {
            if (rs.dirty) {
                Outskirts.renderEngine.getModelRenderer().drawOutline(new AABB(vec3(rs.position()), vec3(rs.position()).add(16)), Colors.RED);
            }
        }
    }

    private static void renderLODSizes() {
        Outskirts.renderEngine.getChunkRenderDispatcher().getRenderSections().forEach(e -> {
            Gui.drawWorldpoint(e.position(), (x,y) -> {
                Gui.drawString("LV: "+ e.calcShouldLodSize(), x,y, Colors.WHITE);
            });
        });
    }

    private static void initCBL(Gui g, boolean dfb, Consumer<Boolean> onchecked) {
        GuiCheckBox cb = (GuiCheckBox)g;
        cb.addOnCheckedListener(e -> {
            onchecked.accept(cb.isChecked());
        });
        cb.setChecked(!dfb); // make sure OnCheckedEvent will be perform. (change checked-status.)
        cb.setChecked(dfb);
    }
}
