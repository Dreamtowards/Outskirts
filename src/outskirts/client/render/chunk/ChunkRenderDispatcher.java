package outskirts.client.render.chunk;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.client.render.VertexBuffer;
import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.event.world.chunk.ChunkUnloadedEvent;
import outskirts.event.world.chunk.section.SectionLoadedEvent;
import outskirts.event.world.chunk.section.SectionUnloadedEvent;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.*;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.ChunkPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.aabb;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.util.logging.Log.LOGGER;

public class ChunkRenderDispatcher {

    private final List<RenderSection> rendersections = new CopyOnIterateArrayList<>();

    public ChunkRenderDispatcher() {

        Events.EVENT_BUS.register(this);

        Thread t = new Thread(new ModelBuildWorker(), "ChunkModelGenWorker");
        t.setPriority(2);
        t.start();
    }

    @EventHandler
    private void sectionLoaded(SectionLoadedEvent e) {
        RenderSection rs = new RenderSection(e.getPosition());
        rs.dirty = true;

        rs.doLoadUp();
        rendersections.add(rs);

        // also updates neiberghts. for ensure seamless mesh.
        markRebuild(aabb(e.getPosition(), 16));
    }

    @EventHandler
    private void sectionUnloaded(SectionUnloadedEvent e) {
        int i = CollectionUtils.removeIf(rendersections,
                rs -> rs.position().equals(e.getPosition()),
                rs -> {
                    rs.proxyentity.getModel();
                    rs.doUnloadDown();
                });
        assert i == 1;
    }

    public RenderSection findSection(Vector3f pos) {
        Vector3f secpos = Vector3f.floor(vec3(pos), 16f);
        for (RenderSection rs : rendersections) {
            if (rs.position().equals(secpos)) return rs;
        }
        return null;
    }

    public boolean markRebuild(Vector3f pos) {
        RenderSection rs = findSection(pos);
        if (rs == null) return false;
        rs.dirty = true;
        return true;
    }
    public final int markRebuild(AABB range) {
        Val c = Val.zero();
        AABB.forGridi(aabb(range).grow(0.1f), 16, p -> {
            if (markRebuild(p))
                c.val++;
        });
        return (int)c.val;
    }

    public List<RenderSection> getRenderSections() {
        return Collections.unmodifiableList(rendersections);
    }

    private class ModelBuildWorker implements Runnable {
        @Override
        public void run() {
            while (Outskirts.isRunning()) {
                for (RenderSection rs : rendersections) {
                    if (rs == null) {
                        LOGGER.info("RS == null. HOW??");
                        continue;
                    }
                    if (rs.dirty) {
                        rs.dirty = false;
                        processRenderSection(rs);

                        SystemUtil.sleep(100);
                    }
                }
                SystemUtil.sleep(100);
            }
        }

        private void processRenderSection(RenderSection rs) {
            Ref<float[]> vertm = Ref.wrap();
            VertexBuffer vbuf = ChunkMeshGen.buildModel(rs, vertm);

            if (vbuf==null) return;

            Outskirts.getScheduler().addScheduledTask(() -> {
                Model model = Loader.loadModel(3,vbuf.posarr(), 2,vbuf.uvarr(), 3,vbuf.normarr(), 1,vertm.value);
                rs.setModel(model);
//                LOGGER.info("MODEL UPLOADED. "+model);
            });
        }
    }
}
