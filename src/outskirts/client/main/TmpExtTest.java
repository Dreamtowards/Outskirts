package outskirts.client.main;

import outskirts.client.Outskirts;
import outskirts.client.render.isoalgorithm.csg.CSG;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.isoalgorithm.sdf.SDF;
import outskirts.client.render.lighting.Light;
import outskirts.event.client.input.CharInputEvent;
import outskirts.material.Material;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.SystemUtil;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.input.Keyboard.KEY_E;
import static org.lwjgl.input.Keyboard.KEY_T;
import static outskirts.client.Outskirts.*;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.aabb;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.event.Events.EVENT_BUS;

public class TmpExtTest {

    public static Light theLight = new Light();

    //todo: GameRule LS   Separator/ NonCollision
    //tod0: DebugV OP. options

    public static int matId = 1;

    public static void init() {

        theLight.position().set(40, 50, 40);
        theLight.color().set(1, 1, 1).scale(1.2f);

        SystemUtil.debugAddKeyHook(KEY_E, () -> {
            theLight.position().set(getPlayer().position());
            renderEngine.getShadowRenderer().getShadowDirection().set(getCamera().getDirection());
        });

        EVENT_BUS.register(CharInputEvent.class, e -> {
            char c = e.getChar();
            if (c >= '0' && c <= '9') {
                matId = c-48;
            }
        });
        SystemUtil.debugAddKeyHook(KEY_T, () -> {
//            Outskirts.getRootGUI().addGui(new GuiWindow(new GuiDebugSnapshot(Outskirts.getRootGUI())));

        });
        SystemUtil.debugAddMouseKeyHook(1, () -> {
            Vector3f p = Outskirts.getRayPicker().getCurrentPoint();
            if (p == null) return;

            Octree.Leaf lf = Outskirts.getWorld().findLeaf(p,null);
            lf.material = Material.REGISTRY.values().get(matId);

            Vector3f mn = Vector3f.floor(vec3(p), lf.size);
            renderEngine.getChunkRenderDispatcher().markRebuild(aabb(mn, lf.size));
        });
        SystemUtil.debugAddMouseKeyHook(2, () -> {
            Vector3f p = Outskirts.getRayPicker().getCurrentPoint();
            if (p==null)return;

            AABB aabb = aabb(vec3(p).sub(3), 6);

            Outskirts.getWorld().forOctrees(aabb, (nd, v) -> {
                TrifFunc FUNCQ = (x, y, z) -> {
                    return SDF.box(vec3(x,y,z).add(v).sub(p), vec3(2,3,2));
                };
                CSG.difference(nd, FUNCQ);
            });

            renderEngine.getChunkRenderDispatcher().markRebuild(aabb);
        });
    }

}
