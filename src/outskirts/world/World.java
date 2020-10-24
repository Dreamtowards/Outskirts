package outskirts.world;

import outskirts.block.Block;
import outskirts.client.Outskirts;
import outskirts.client.material.Model;
import outskirts.client.render.Light;
import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.entity.Entity;
import outskirts.entity.player.EntityPlayer;
import outskirts.init.ex.Models;
import outskirts.init.Textures;
import outskirts.physics.dynamics.DiscreteDynamicsWorld;
import outskirts.storage.Savable;
import outskirts.storage.dat.DATArray;
import outskirts.storage.dat.DATObject;
import outskirts.util.GameTimer;
import outskirts.util.logging.Log;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;
import outskirts.world.gen.ChunkGenerator;

import javax.annotation.Nullable;
import java.util.*;

import static outskirts.util.Maths.floor;
import static outskirts.util.Maths.mod;

public abstract class World implements Savable { // impl Tickable ..?

    private List<Entity> entities = new ArrayList<>();

    public List<Light> lights = new ArrayList<>();

    public DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld();
    public float tmpTickFactor = 1;

    private Map<Long, Chunk> loadedChunks = new HashMap<>();

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


    public void setBlock(int x, int y, int z, Block b) {
        Chunk chunk = getLoadedChunk(floor(x, 16), floor(z, 16));
        assert chunk != null;
        chunk.setBlock(mod(x, 16), y, mod(z, 16), b);

        chunk.markedRebuildModel=true;
    }

    public Block getBlock(int x, int y, int z) {
        Chunk chunk = getLoadedChunk(floor(x, 16), floor(z, 16));
        if (chunk == null)
            return null;
        if (y < 0) return null;

        return chunk.getBlock(mod(x, 16), y, mod(z, 16));
    }

    public Chunk provideChunk(int x, int z) {
        Chunk chunk = getLoadedChunk(x, z);
        if (chunk == null) {
//            chunk = load.

            chunk = new ChunkGenerator().generate(x, z);

            loadedChunks.put(ChunkPos.asLong(chunk.x, chunk.z), chunk);
            addEntity(chunk.proxyEntity);
            chunk.proxyEntity.setModel(Models.GEO_CUBE);
        }
        return chunk;
    }

    public void unloadChunk(Chunk chunk) {
        loadedChunks.remove(ChunkPos.asLong(chunk.x, chunk.z));
        removeEntity(chunk.proxyEntity);
    }

    @Nullable
    public Chunk getLoadedChunk(int x, int z) {
        return loadedChunks.get(ChunkPos.asLong(x, z));
    }

    public Collection<Chunk> getLoadedChunks() {
        return Collections.unmodifiableCollection(loadedChunks.values());
    }

    public void onTick() {

        Outskirts.getProfiler().push("Physics");
        if (tmpTickFactor != 0)
        dynamicsWorld.stepSimulation(1f/GameTimer.TPS *tmpTickFactor);
        Outskirts.getProfiler().pop();


        for (Chunk c : loadedChunks.values()) {
            if (c.markedRebuildModel) {
                c.markedRebuildModel = false;

                Outskirts.getScheduler().addScheduledTask(() -> {
                    Model model = new ChunkModelGenerator().buildModel(ChunkPos.of(c), this);
//                    c.proxyEntity.getMaterial().setDiffuseMap(Loader.loadTexture(new Identifier("materials/mc/end_stone.png").getInputStream()));
                    c.proxyEntity.getMaterial().setDiffuseMap(Block.TEXTURE_ATLAS.getAtlasTexture());
                    c.proxyEntity.getMaterial().setNormalMap(Textures.CONTAINER);
                    c.proxyEntity.setModel(model);
                });
            }
        }

//        Vector3f cenPos = Outskirts.getPlayer().getPosition();
//        int cenX=floor(cenPos.x,16), cenZ=floor(cenPos.z,16);
        int sz = 3;
        for (int i = -sz;i <= sz;i++) {
            for (int j = -sz;j <= sz;j++) {
                provideChunk(i*16, j*16);
            }
        }
//        for (Chunk c : getLoadedChunks().toArray(new Chunk[0])) {
//            if (Math.abs(c.x-cenX) > sz*16 || Math.abs(c.z-cenZ) > sz*16)
//                unloadChunk(c);
//        }
    }

    @Override
    public void onRead(DATObject mp) {
        // clear first.?

        List lsEntities = (List)mp.get("entities");
        for (Object o : lsEntities) {
            addEntity(Entity.createEntity((DATObject)o));
        }
        Log.LOGGER.info("read entities: {}", lsEntities.size());

    }

    @Override
    public Map onWrite(DATObject mp) {
        DATArray lsEntities = new DATArray();
        for (Entity entity : entities) {
            if (entity instanceof EntityPlayer)
                continue;
            lsEntities.add(entity.onWrite(new DATObject()));
        }
        mp.put("entities", lsEntities);
        Log.LOGGER.info("write entities: {}", lsEntities.size());

        DATObject mpMetadata = new DATObject();
        mpMetadata.put("modify_time", System.currentTimeMillis());
        mp.put("metadata", mpMetadata);

        return mp;
    }
}
