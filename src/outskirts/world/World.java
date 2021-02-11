package outskirts.world;

import outskirts.client.Outskirts;
import outskirts.client.render.chunk.ChunkRenderDispatcher;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.lighting.Light;
import outskirts.entity.Entity;
import outskirts.event.Events;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.event.world.chunk.ChunkUnloadedEvent;
import outskirts.material.Material;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.dynamics.DiscreteDynamicsWorld;
import outskirts.util.GameTimer;
import outskirts.util.Ref;
import outskirts.util.Tickable;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;
import outskirts.world.gen.ChunkGenerator;
import outskirts.world.storage.ChunkLoader;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;

import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;
import static outskirts.util.Maths.floor;
import static outskirts.util.Maths.mod;
import static outskirts.util.logging.Log.LOGGER;

public abstract class World implements Tickable {

    private final List<Entity> entities = new ArrayList<>();

    public List<Light> lights = new ArrayList<>(); // better get from entities.

    public DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld();

    private Map<Long, Chunk> loadedChunks = new HashMap<>();

    private ChunkGenerator chunkGenerator = new ChunkGenerator();

    private ChunkLoader chunkLoader = new ChunkLoader();

    public ChunkRenderDispatcher crd = new ChunkRenderDispatcher();

    public void addEntity(Entity entity) {
        assert !entities.contains(entity);
        synchronized (entities) {
            entities.add(entity);
        }
        dynamicsWorld.addCollisionObject(entity.getRigidBody());
        entity.setWorld(this);
    }

    public void removeEntity(Entity entity) {
        assert entities.contains(entity);
        synchronized (entities) {
            entities.remove(entity);
        }
        dynamicsWorld.removeCollisionObject(entity.getRigidBody());
        entity.setWorld(null);
    }

    public List<Entity> getEntities() {
        synchronized (entities) {
            return Collections.unmodifiableList(entities);
        }
    }

    public List<Entity> getEntities(AABB contains) {
        // todo: Accumulate algorithms. use of B.P.
        List<Entity> entities = new ArrayList<>();
        synchronized (this.entities) {
            for (Entity e : getEntities()) {
                if (contains.containsEqLs(e.position()))
                    entities.add(e);
            }
        }
        return entities;
    }


    // reduc: Instead by raycast.
    // use for gen on ground top
//    public final int getHighestBlock(int x, int z) {
//        Block b;
//        for (int y = 255;y >= 0;y--) {
//            if ((b=getBlock(x, y, z)) != null && b.v > 0)
//                return y;
//        }
//        return -1;
//    }

    public Octree.Internal getOctree(Vector3f p) {
        Chunk chunk = getLoadedChunk(p);
        if (chunk == null) return null;
        return chunk.octree(p.y);
    }
    public void forOctrees(AABB aabb, BiConsumer<Octree, Vector3f> visitor) {
        AABB.forGrid(aabb, 16, v -> {
            Octree nd = getOctree(v);
            if (nd != null)
                visitor.accept(nd, v);
        });
    }

    public Octree.Leaf findLeaf(Vector3f p, Ref<Octree.Internal> lp) {
        Octree.Internal node = getOctree(p);
        if (node==null) return null;
        Vector3f rp = Vector3f.mod(vec3(p), 16f).scale(1/16f);
        return Octree.findLeaf(node, rp, lp);
    }

    /**
     * @param x,z world_coordinate.any
     */
    public final Chunk provideChunk(float x, float z) {
        Chunk chunk = getLoadedChunk(x, z);
        if (chunk == null) {
            ChunkPos chunkpos = ChunkPos.of(x, z);
//            chunk = chunkLoader.loadChunk(this, chunkpos);

            if (chunk == null) {
                chunk = chunkGenerator.generate(chunkpos, this);
            }

            loadedChunks.put(ChunkPos.asLong(chunk.x, chunk.z), chunk);
//            Chunk finalChunk = chunk;
//            Outskirts.getScheduler().addScheduledTask(() -> addEntity(finalChunk.proxyEntity));
//            for (int dx=-1;dx<=0;dx++) {
//                for (int dz=-1;dz<=0;dz++) {
//                    Chunk c = getLoadedChunk(x+dx*16, z+dz*16);
//                    if (c!=null)c.markedRebuildModel=true;
//                }
//            }

//            tryPopulate(chunkpos);

            Chunk fchunk = chunk;
            Outskirts.getScheduler().addScheduledTask(() -> Events.EVENT_BUS.post(new ChunkLoadedEvent(fchunk)));
        }
        return chunk;
    }

    private void tryPopulate(ChunkPos chunkpos) {
        for (int dx = -1;dx <= 1;dx++) {
            for (int dz = -1;dz <= 1;dz++) {
                Chunk chunk = getLoadedChunk(chunkpos.x+dx*16, chunkpos.z+dz*16);
                if (chunk != null && !chunk.populated && isNeibghersAllLoaded(chunk.x, chunk.z)) {
                    chunkGenerator.populate(this, ChunkPos.of(chunk));
                    chunk.populated = true;
                }
            }
        }
    }

    /**
     * Default Chunk Populate Condition.
     * allow populate if neibgher chunks already loaded.
     * else if just directly populate may cause Massive-Chain-Generation.
     */
    private boolean isNeibghersAllLoaded(int chunkX, int chunkZ) {
        for (int dx = -1;dx <= 1;dx++) {
            for (int dz = -1;dz <= 1;dz++) {
                if (getLoadedChunk(chunkX+dx*16, chunkZ+dz*16) == null)
                    return false;
            }
        }
        return true;
    }

    public void unloadChunk(Chunk chunk) {
        loadedChunks.remove(ChunkPos.asLong(chunk.x, chunk.z));

        Outskirts.getScheduler().addScheduledTask(() -> Events.EVENT_BUS.post(new ChunkUnloadedEvent(chunk)));

        chunkLoader.saveChunk(chunk);
    }

    /**
     * @param x,z world_coordinate.any
     */
    @Nullable
    public Chunk getLoadedChunk(float x, float z) {
        return loadedChunks.get(ChunkPos.asLong(x, z));
    }
    public final Chunk getLoadedChunk(ChunkPos chunkpos) {
        return getLoadedChunk(chunkpos.x, chunkpos.z);
    }
    public final Chunk getLoadedChunk(Vector3f p) {
        return getLoadedChunk(p.x, p.z);
    }

    public final Collection<Chunk> getLoadedChunks() {
        return Collections.unmodifiableCollection(loadedChunks.values());
    }

    @Override
    public void onTick() {

        Outskirts.getProfiler().push("Physics");
        dynamicsWorld.stepSimulation(1f/GameTimer.TPS);
        Outskirts.getProfiler().pop("Physics");

        for (Entity entity : entities.toArray(new Entity[0])) {
            entity.onTick();
        }



    }

    public static int sz=1;
    {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Vector3f cenPos = Outskirts.getPlayer().position();
                    int cenX=floor(cenPos.x,16), cenZ=floor(cenPos.z,16);
    //                int sz = 1;
                    for (int i = -sz;i <= sz;i++) {
                        for (int j = -sz;j <= sz;j++) {
                            provideChunk(cenX+i*16, cenZ+j*16);
                        }
                    }
                    for (Chunk c : new ArrayList<>(getLoadedChunks())) {
                        if (Math.abs(c.x-cenX) > sz*16 || Math.abs(c.z-cenZ) > sz*16 || Outskirts.getWorld() == null)
                            unloadChunk(c);
                    }
                    if (Outskirts.getWorld() == null)
                        break;


                    Thread.sleep(20);
                } catch (Exception ex) {
                    ex.printStackTrace();
//                    break;
                }
            }
            LOGGER.info("ChunkLoad Thread Done.");
        });
        t.setDaemon(true);
        t.start();
    }
}
