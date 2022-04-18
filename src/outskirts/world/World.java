package outskirts.world;

import outskirts.client.Outskirts;
import outskirts.entity.Entity;
import outskirts.event.Events;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.event.world.chunk.ChunkUnloadedEvent;
import outskirts.physics.dynamics.DiscreteDynamicsWorld;
import outskirts.util.GameTimer;
import outskirts.util.Tickable;
import outskirts.util.vector.Vector3f;
import outskirts.world.gen.ChunkGenerator;

import java.util.*;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.aabb;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.util.Maths.floor;
import static outskirts.util.Maths.mod;

public abstract class World implements Tickable {

    private final List<Entity> entities = new ArrayList<>();

    private final Map<Vector3f, Chunk> loadedChunks = new HashMap<>();

    public final DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld();

    private final ChunkGenerator chunkGenerator = new ChunkGenerator();
//    private ChunkLoader chunkLoader = new ChunkLoader();

    // 24000, 0==sunrise.
    public float daytime;

    public void addEntity(Entity entity) {
        assert !entities.contains(entity) : "Failed add the entity, already existed.";
        synchronized (entities) {
            entities.add(entity);
        }
        dynamicsWorld.addCollisionObject(entity.getRigidBody());
        entity.setWorld(this);
    }

    public void removeEntity(Entity entity) {
        assert entities.contains(entity) : "Failed remove the entity, not exists.";
        synchronized (entities) {
            entities.remove(entity);
        }
        dynamicsWorld.removeCollisionObject(entity.getRigidBody());
        entity.setWorld(null);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public final Chunk provideChunk(Vector3f chunkpos) {
        Chunk chunk = getLoadedChunk(chunkpos);
        if (chunk != null)
            return chunk;
        // chunk = chunkLoader.loadChunk(this, chunkpos);

        if (chunk == null) {
            chunk = chunkGenerator.generate(chunkpos, this);
        }

        loadedChunks.put(chunkpos, chunk);

//            tryPopulate(chunkpos);
        Events.EVENT_BUS.post(new ChunkLoadedEvent(chunk));
        return chunk;
    }

//    private void tryPopulate(ChunkPos chunkpos) {
//        for (int dx = -1;dx <= 1;dx++) {
//            for (int dz = -1;dz <= 1;dz++) {
//                Chunk chunk = getLoadedChunk(chunkpos.x+dx*16, chunkpos.z+dz*16);
//                if (chunk != null && !chunk.populated && isNeibghersAllLoaded(chunk.x, chunk.z)) {
//                    chunkGenerator.populate(this, ChunkPos.of(chunk));
//                    chunk.populated = true;
//                }
//            }
//        }
//    }

    /**
     * Default Chunk Populate Condition.
     * allow populate if neibgher chunks already loaded.
     * else if just directly populate may cause Massive-Chain-Generation.
     */
//    private boolean isNeibghersAllLoaded(int chunkX, int chunkZ) {
//        for (int dx = -1;dx <= 1;dx++) {
//            for (int dz = -1;dz <= 1;dz++) {
//                if (getLoadedChunk(chunkX+dx*16, chunkZ+dz*16) == null)
//                    return false;
//            }
//        }
//        return true;
//    }

    public void unloadChunk(Chunk chunk) {
        Chunk tmp = loadedChunks.remove(chunk.getPosition());
        assert tmp != null : "Failed to unload. no such chunk.";

//        chunkLoader.saveChunk(chunk);

        Events.EVENT_BUS.post(new ChunkUnloadedEvent(chunk));
    }

    public final Chunk getLoadedChunk(Vector3f chunkpos) {
        return loadedChunks.get(chunkpos);
    }

    public final Collection<Chunk> getLoadedChunks() {
        return Collections.unmodifiableCollection(loadedChunks.values());
    }

    @Override
    public void onTick() {

        entities.forEach(e -> {
            e.getPrevPosition().set(e.position());
        });

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

    public static int sz=0;
}
