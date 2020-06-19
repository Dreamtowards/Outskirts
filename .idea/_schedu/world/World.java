package outskirts.world;

import outskirts.entity.Entity;
import outskirts.event.Events;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.dynamic.DiscreteDynamicsWorld;
import outskirts.util.GameTimer;
import outskirts.util.Maths;
import outskirts.util.Tickable;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.Octree;
import outskirts.world.gen.ChunkGenerator;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class World implements Tickable {

    private List<Entity> entities = new ArrayList<>();

    private Map<Long, Chunk> chunks = new HashMap<>();
    private ChunkGenerator chunkGenerator = new ChunkGenerator();

    private DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld();

    private File worldDirectory;

    private ChunkLoader chunkLoader = new ChunkLoader(this);

    public World(File worldDirectory) {
        this.worldDirectory = worldDirectory;
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        dynamicsWorld.addCollisionObject(entity.getRigidBody()); //TODO: really?
    }

    //now just for debug
    public DiscreteDynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }

    public Octree getOctree(float x, float y, float z, int depth, boolean creating) {
        Chunk chunk = getLoadedChunk(x, z);
//        Validate.notNull(chunk, "The chunk has not been loaded yet.");
        if (chunk == null)
            return null;

        return chunk.getOctree(x, y, z, depth, creating);
    }

    public final Octree getOctree(float x, float y, float z, int depth) {
        return getOctree(x, y, z , depth, false);
    }
    public final Octree getOctree(Vector3f blockPos, int depth) {
        return getOctree(blockPos.x, blockPos.y, blockPos.z, depth, false);
    }
    public final Octree getOctree(Vector3f blockPos, int depth, boolean creating) {
        return getOctree(blockPos.x, blockPos.y, blockPos.z, depth, creating);
    }


    @Override
    public void onTick() {


        dynamicsWorld.stepSimulation(1f / GameTimer.TPS);
    }


    public List<AABB> getAABBs(AABB range) {
        List<AABB> result = new ArrayList<>();

        AABB tmpAabb = new AABB();
        // foreach octrees
        for (Octree octree : getRootOctrees(range)) {
            Octree.forChildren(octree, child -> {
                if (child.hasBody()) {
                    if (AABB.intersects(range, child.getAABB(tmpAabb))) {
                        result.add(new AABB(tmpAabb));
                    }
                }
            });
        }

        for (Entity entity : entities) {
            AABB entityAABB = entity.getRigidBody().getAABB();
            if (AABB.intersects(range, entityAABB)) {
                result.add(entityAABB);
            }
        }

        return result;
    }

    private List<Octree> getRootOctrees(AABB range) {
        List<Octree> result = new ArrayList<>();

        Vector3f chunkMin = Vector3f.unit(new Vector3f(range.min), 16f); //actually is integer (non fraction
        Vector3f chunkMax = Vector3f.unit(new Vector3f(range.max), 16f);

        for (float x = chunkMin.x;x <= chunkMax.x;x += 16f) {
            for (float z = chunkMin.z;z <= chunkMax.z;z += 16f) {
                Chunk chunk = Objects.requireNonNull(getLoadedChunk(x, z));

                for (float y = Math.max(chunkMin.y, 0);y <= chunkMax.y;y += 16f) {
                    result.add(chunk.octree(y));
                }
            }
        }
        return result;
    }

    public Chunk provideChunk(int x, int z) {
        x = Maths.unit(x, 16);
        z = Maths.unit(z, 16);

        Chunk chunk = getLoadedChunk(x, z);
        if (chunk == null) {
            //load or gen
            chunk = chunkLoader.loadChunk(x, z);

            if (chunk == null) {
                chunk = chunkGenerator.generate(x, z, this);
            }

            chunks.put(Chunk.posLong(x, z), chunk);

            Events.EVENT_BUS.post(new ChunkLoadedEvent(chunk));
        }
        return chunk;
    }

    /**
     * @param x,z world coordinates. should int. but float is just for more convenient for not (int)v cast
     */
    @Nullable
    public Chunk getLoadedChunk(float x, float z) {
        return chunks.get(Chunk.posLong(
                Maths.unit(x, 16),
                Maths.unit(z, 16)
        ));
    }

    public void saveAllChunks() throws IOException {
        for (Chunk chunk : chunks.values()) {
            chunkLoader.saveChunk(chunk);
        }
    }

    public File getWorldDirectory() {
        return worldDirectory;
    }
}
