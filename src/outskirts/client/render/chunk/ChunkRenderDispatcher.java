package outskirts.client.render.chunk;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.client.render.VertexBuffer;
import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.event.world.chunk.ChunkUnloadedEvent;
import outskirts.util.Ref;
import outskirts.util.SystemUtil;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.ChunkPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;
import static outskirts.util.logging.Log.LOGGER;

public class ChunkRenderDispatcher {

    private final List<RenderSection> rendersections = new ArrayList<>();

    public ChunkRenderDispatcher() {

        Events.EVENT_BUS.register(this);

        Thread t = new Thread(new ModelBuildWorker(), "ChunkModelGenWorker");
        t.setDaemon(true);
        t.setPriority(2);
        t.start();
    }

    @EventHandler
    private void chunkLoaded(ChunkLoadedEvent e) {
        ChunkPos cpos = ChunkPos.of(e.getChunk());
        for (int i = 0;i < 16;i++) {
            RenderSection rs = new RenderSection(vec3(cpos.x, i*16, cpos.z));
            rs.dirty = true;

//            for (int j = 0;j<3;j++) {
//                markRebuild(vec3(rs.position()).addv(j, -16));
//            }
//            markRebuild(vec3(rs.position()).add(0, -16, -16));
//            markRebuild(vec3(rs.position()).add(-16, 0, -16));
//            markRebuild(vec3(rs.position()).add(-16, -16, 0));

            rs.doLoadUp();
            synchronized (rendersections) {
                rendersections.add(rs);
            }
        }
    }

    @EventHandler
    private void chunkUnloaded(ChunkUnloadedEvent e) {
        ChunkPos cpos = ChunkPos.of(e.getChunk());
        synchronized (rendersections) {
            rendersections.removeIf(rs -> {
                if (rs.position().x == cpos.x && rs.position().z == cpos.z) {
                    rs.doUnloadDown();
                    return true;
                } else return false;
            });
        }
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

    private class ModelBuildWorker implements Runnable {
        @Override
        public void run() {
            while (true) {
                for (RenderSection rs : new ArrayList<>(rendersections)) {
                    tryProcessRenderSection(rs);
                }
                SystemUtil.sleep(100);
            }
        }

        private void tryProcessRenderSection(RenderSection rs) {
            if (Outskirts.getWorld() == null) return;

            if (rs.dirty) {
                rs.dirty = false;

                Ref<float[]> vertm = Ref.wrap();
                VertexBuffer vbuf = ChunkMeshGen.buildModel(rs, vertm);

                if (vbuf==null) return;

                Outskirts.getScheduler().addScheduledTask(() -> {
                    Model model = Loader.loadModel(3,vbuf.posarr(), 2,vbuf.uvarr(), 3,vbuf.normarr(), 3,vbuf.posarr(), 1,vertm.value);
                    rs.setModel(model);
                    LOGGER.info("MODEL UPLOADED.");
                });
            }
        }

        private float[] genVertBaryCoord(int vn) {
            float[] barycd = new float[vn*3];
            for (int i = 0;i < vn;i+=3) {
                barycd[i*3]=1;   barycd[i*3+1]=0; barycd[i*3+2]=0;
                barycd[i*3+3]=0; barycd[i*3+4]=1; barycd[i*3+5]=0;
                barycd[i*3+6]=0; barycd[i*3+7]=0; barycd[i*3+8]=1;
            }
            return barycd;
        }

        private float[] genVertMaterialId(int vn, int[] vertm) {
            float[] vmids = new float[vn*3];
            for (int i = 0;i < vn;i+=3) {
                int v1Id = vertm[i];
                int v2Id = vertm[i+1];
                int v3Id = vertm[i+2];
                for (int j=0;j<3;j++) {
                    vmids[i*3+j*3] =   v1Id;
                    vmids[i*3+j*3+1] = v2Id;
                    vmids[i*3+j*3+2] = v3Id;
                }
            }
            return vmids;
        }
    }
}
