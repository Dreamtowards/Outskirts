package outskirts.client.render.chunk;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.Octree;
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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.*;
import static outskirts.util.logging.Log.LOGGER;
import static outskirts.util.vector.Vector3f.abs;

public class ChunkRenderDispatcher {

    private final List<RenderSection> rendersections = new CopyOnWriteArrayList<>();

    public ChunkRenderDispatcher() {

        Events.EVENT_BUS.register(this);

        Thread t = new Thread(new ModelBuildWorker(), "ChunkModelGenWorker");
        t.setPriority(2);
        t.start();
    }

    @EventHandler
    private void onSectionLoaded(SectionLoadedEvent e) {
        RenderSection rs = new RenderSection(e.getPosition());
        rs.dirty = true;

        rs.doLoadUp();
        rendersections.add(rs);

        // translate -Epsolion: also updates neg-neiberghts. for ensure seamless mesh.
        markRebuild(aabb(e.getPosition(), 16).translate(-0.1f));
    }

    @EventHandler
    private void onSectionUnloaded(SectionUnloadedEvent e) {
        int i = CollectionUtils.removeIf(rendersections,
                rs -> rs.position().equals(e.getPosition()),
                RenderSection::doUnloadDown);
        assert i == 1;
    }

    public RenderSection findSection(Vector3f p) {
        Vector3f basepos = Vector3f.floor(vec3(p), 16f);
        for (RenderSection rs : rendersections) {
            if (rs.position().equals(basepos)) return rs;
        }
        return null;
    }
    private Octree findLOD(Vector3f pos) {
        RenderSection rs = findSection(pos);
        if (rs == null) return null;
        if (rs.cachedLod == null) {
            rs.computeLOD();
        }
        return rs.cachedLod;
    }
    static float calculateLodSize(Vector3f basepos, Vector3f playerpos) {
        Vector3f midpoint = vec3(basepos).add(8f);  // section midpoint instead of basepoint. better accuracy.
        int lv = (int)(midpoint.sub(playerpos).length() / 16f);
        if (lv == 0)
            return 0;  // the near sections, just do not lod.
        lv /= 2f;  // dont lose detail too soon.
        return Math.min(1 << lv, 8);  // clamp size upon 8, then root octree won't been a leaf.
    }

    public boolean markRebuild(Vector3f p) {
        RenderSection rs = findSection(p);
        if (rs == null) return false;
        rs.dirty = true;
        rs.lodDirty = true;
        return true;
    }
    public final int markRebuild(AABB rangei) {
        Val c = Val.zero();
        AABB.forGridi(aabb(rangei), 16f, p -> {
            if (markRebuild(p))
                c.val++;
        });
        return (int)c.val;
    }

    public final List<RenderSection> getRenderSections() {
        return rendersections;
    }

    private void doSortForPriority() {
        CollectionUtils.insertionSort(rendersections, Comparator.comparing(e -> e.cachedLodSize));
    }

    private class ModelBuildWorker implements Runnable {
        @Override
        public void run() {
            while (Outskirts.isRunning()) {
                List<RenderSection> rendersections = new ArrayList<>(ChunkRenderDispatcher.this.rendersections);

                for (RenderSection rs : rendersections) {
                    if (rs.cachedLodSize != rs.calcShouldLodSize()) {
                        // also let neg sections update. for seamless.
                        markRebuild(aabb(rs.position(), 16).translate(-0.1f));
                    }
                }

                for (RenderSection rs : rendersections) {
                    if (rs.lodDirty) {
                        rs.lodDirty = false;
                        rs.computeLOD();
                    }
                }

                doSortForPriority();  // looks some useless. the React dosen't really work as interating all.

                for (RenderSection rs : rendersections) {
                    if (rs.dirty) {
                        rs.dirty = false;

                        processRenderSection(rs);
                    }
                }

                SystemUtil.sleep(100);
            }
        }

        private void processRenderSection(RenderSection rs) {  if (Outskirts.getWorld() == null) return;
            Ref<float[]> vertm = Ref.wrap();
            VertexBuffer vbuf = ChunkMeshGen.buildModel(rs, vertm, ChunkRenderDispatcher.this::findLOD);

            if (vbuf==null) return;

            Outskirts.getScheduler().addScheduledTask(() -> {
                Model model = Loader.loadModel(3,vbuf.posarr(), 2,vbuf.uvarr(), 3,vbuf.normarr(), 1,vertm.value);
                rs.setModel(model);
//                LOGGER.info("MODEL UPLOADED. "+model);
            });
        }
    }
}
