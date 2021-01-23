package outskirts.client.render.chunk;

import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.client.render.VertexBuffer;
import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.event.world.chunk.ChunkUnloadedEvent;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.ChunkPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static outskirts.client.render.isoalgorithm.distfunc.VecCon.vec3;

public class ChunkRenderDispatcher {

    private final List<RenderSection> rendersections = new ArrayList<>();

    private ChunkModelGenWorker worker = new ChunkModelGenWorker(this::pollTask);

    public ChunkRenderDispatcher() {

        Events.EVENT_BUS.register(this);

        Thread t = new Thread(worker, "ChunkModelGenWorker");
        t.setDaemon(true);
        t.start();
    }

    @EventHandler
    private void chunkLoaded(ChunkLoadedEvent e) {
        ChunkPos cpos = ChunkPos.of(e.getChunk());
        for (int i = 0;i < 16;i++) {
            RenderSection rs = new RenderSection();
            rs.position().set(cpos.x, i*16, cpos.z);
            rs.markDirty();
            e.getWorld().addEntity(rs.proxyentity());
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
                    e.getWorld().removeEntity(rs.proxyentity());
                    return true;
                } else return false;
            });
        }
    }

    private RenderSection findRenderSection(Vector3f pos) {
        Vector3f secpos = Vector3f.floor(vec3(pos), 16f);
        for (RenderSection rs : rendersections) {
            if (rs.position().equals(secpos)) return rs;
        }
        return null;
    }

    public void markForRebuild(Vector3f pos) {
        findRenderSection(pos).markDirty();
    }

    public RenderSection pollTask() {
        synchronized (rendersections) {
            for (RenderSection rs : rendersections) {
                if (rs.isDirty()) {
                    rs.clearDirty();
                    return rs;
                }
            }
            return null;
        }
    }



}
