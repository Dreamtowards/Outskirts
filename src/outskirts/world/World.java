package outskirts.world;

import outskirts.client.Outskirts;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.lighting.Light;
import outskirts.entity.Entity;
import outskirts.event.Events;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.event.world.chunk.ChunkUnloadedEvent;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.dynamics.DiscreteDynamicsWorld;
import outskirts.util.CopyOnIterateArrayList;
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

import static outskirts.client.render.isoalgorithm.sdf.Vectors.aabb;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.util.Maths.floor;
import static outskirts.util.Maths.mod;
import static outskirts.util.logging.Log.LOGGER;

public abstract class World implements Tickable {

    private final List<Entity> entities = new ArrayList<>();

    private Map<Long, Chunk> loadedChunks = new HashMap<>();
    /**
     * K:vec3 MOD 16 == 0.
     */
    private Map<Vector3f, Chunk> loadedSections = new HashMap<>();

    public List<Light> lights = new ArrayList<>(); // better to get from entities.

    public DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld();

    private ChunkGenerator chunkGenerator = new ChunkGenerator();
    private ChunkLoader chunkLoader = new ChunkLoader();

    // 24000, 0==sunrise.
    public float daytime;

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

    /**
     * fc: if contains entity.pos.
     * fi: if intersects entity.volume.
     */
    public List<Entity> getEntitiesfc(AABB contains) {
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


    public Octree.Internal getOctree(Vector3f p) {
        Chunk chunk = getLoadedChunk(p);
        if (chunk == null) return null;
        return chunk.octree(p.y);
    }
    public final void forOctrees(AABB aabb, BiConsumer<Octree, Vector3f> visitor) {
        AABB.forGridi(aabb, 16, v -> {
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

        chunkLoader.saveChunk(chunk);

        // Unload Sections.
        for (int k : new ArrayList<>(chunk.getOctrees().keySet())) {
            chunk.octree(k, null);
        }

        Outskirts.getScheduler().addScheduledTask(() -> Events.EVENT_BUS.post(new ChunkUnloadedEvent(chunk)));
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

        for (Entity entity : new ArrayList<>(entities)) {
            entity.onTick();
        }

        daytime += Outskirts.isAltKeyDown() ? 80 : 2;
        if (daytime > 24000)
            daytime = 0;


    }

    public static int sz=3;
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
