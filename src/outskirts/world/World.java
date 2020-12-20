package outskirts.world;

import outskirts.block.Block;
import outskirts.client.Outskirts;
import outskirts.client.material.Model;
import outskirts.client.render.Light;
import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.entity.Entity;
import outskirts.event.Events;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.event.world.chunk.ChunkMeshBuildedEvent;
import outskirts.init.ex.Models;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.dynamics.DiscreteDynamicsWorld;
import outskirts.util.GameTimer;
import outskirts.util.Tickable;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;
import outskirts.world.gen.ChunkGenerator;
import outskirts.world.storage.ChunkLoader;

import javax.annotation.Nullable;
import java.util.*;

import static outskirts.util.Maths.floor;
import static outskirts.util.Maths.mod;
import static outskirts.util.logging.Log.LOGGER;

public abstract class World implements Tickable {

    private List<Entity> entities = new ArrayList<>();

    public List<Light> lights = new ArrayList<>();

    public DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld();

    private Map<Long, Chunk> loadedChunks = new HashMap<>();

    private ChunkGenerator chunkGenerator = new ChunkGenerator();

    private ChunkLoader chunkLoader = new ChunkLoader();

    public void addEntity(Entity entity) {
        entity.setWorld(this);
        entities.add(entity);
        dynamicsWorld.addCollisionObject(entity.getRigidBody());
    }

    public void removeEntity(Entity entity) {
        entity.setWorld(null);
        entities.remove(entity);
        dynamicsWorld.removeCollisionObject(entity.getRigidBody());
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public List<Entity> getEntities(AABB contains) {
        // todo: Accumulate algorithms. use of B.P.
        List<Entity> entities = new ArrayList<>();
        for (Entity e : getEntities()) {
            if (contains.containsEqLs(e.position()))
                entities.add(e);
        }
        return entities;
    }


    public void setBlock(int x, int y, int z, Block b) {
        Chunk chunk = provideChunk(floor(x, 16), floor(z, 16));
//        assert chunk != null;
        chunk.setBlock(mod(x, 16), y, mod(z, 16), b);

        for (int dx=-1;dx<=1;dx++) {
            for (int dz=-1;dz<=1;dz++) {
                Chunk c = getLoadedChunk(x+dx, z+dz);
                if (c != null)
                    c.markedRebuildModel=true;
            }
        }
    }
    public void setBlock(float x, float y, float z, Block b) {
        setBlock((int)x, (int)y, (int)z, b);
    }
    public void setBlock(Vector3f blockpos, Block b) {
        setBlock(blockpos.x, blockpos.y, blockpos.z, b);
    }

    public Block getBlock(int x, int y, int z) {
        Chunk chunk = getLoadedChunk(floor(x, 16), floor(z, 16));
        if (chunk == null)
            return null;
        if (y < 0 || y >= 256) return null;

        return chunk.getBlock(mod(x, 16), y, mod(z, 16));
    }
    public Block getBlock(float x, float y, float z) {
        return getBlock((int)x, (int)y, (int)z);
    }
    public Block getBlock(Vector3f blockpos) {
        return getBlock(blockpos.x, blockpos.y, blockpos.z);
    }

    public final int getHighestBlock(int x, int z) {
        for (int y = 255;y >= 0;y--) {
            if (getBlock(x, y, z) != null)
                return y;
        }
        return -1;
    }

    public Chunk provideChunk(float x, float z) {
        Chunk chunk = getLoadedChunk(x, z);
        if (chunk == null) {
            ChunkPos chunkpos = ChunkPos.of(x, z);
//            chunk = chunkLoader.loadChunk(this, chunkpos);

            if (chunk == null) {
                chunk = chunkGenerator.generate(chunkpos, this);
            }

            loadedChunks.put(ChunkPos.asLong(chunk.x, chunk.z), chunk);
            chunk.proxyEntity.setModel(Models.GEO_CUBE);
            Chunk finalChunk = chunk;
            Outskirts.getScheduler().addScheduledTask(() -> addEntity(finalChunk.proxyEntity));
            for (int dx=-1;dx<=0;dx++) {
                for (int dz=-1;dz<=0;dz++) {
                    Chunk c = getLoadedChunk(x+dx*16, z+dz*16);
                    if (c!=null)c.markedRebuildModel=true;
                }
            }

            tryPopulate(chunkpos);

            Events.EVENT_BUS.post(new ChunkLoadedEvent(chunk));
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
        Outskirts.getScheduler().addScheduledTask(() -> removeEntity(chunk.proxyEntity));

        chunkLoader.saveChunk(chunk);
    }

    @Nullable
    public Chunk getLoadedChunk(float x, float z) {
        return loadedChunks.get(ChunkPos.asLong(x, z));
    }

    public Collection<Chunk> getLoadedChunks() {
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

    public static int sz=2;
    {
        new Thread(() -> {
            while (true) {
                try {
                    Vector3f cenPos = Outskirts.getPlayer().getPosition();
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

                    for (Chunk c : loadedChunks.values()) {
                        if (c.markedRebuildModel) {
                            c.markedRebuildModel = false;

                            Outskirts.getScheduler().addScheduledTask(() -> {
                                Model model = new ChunkModelGenerator().buildModel(ChunkPos.of(c), this);
                                c.proxyEntity.setModel(model.indices.length == 0 ? Models.GEO_SPHERE : model);
                                Events.EVENT_BUS.post(new ChunkMeshBuildedEvent(c));
                            });
                        }
                    }

                    Thread.sleep(20);
                } catch (Exception ex) {
                    ex.printStackTrace();
//                    break;
                }
            }
            LOGGER.info("ChunkLoad Thread Done.");
        }).start();
    }
}
