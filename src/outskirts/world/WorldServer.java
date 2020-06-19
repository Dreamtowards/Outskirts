package outskirts.world;

import outskirts.entity.Entity;
import outskirts.entity.player.EntityPlayer;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.event.Event;
import outskirts.event.Events;
import outskirts.event.world.terrain.TerrainLoadedEvent;
import outskirts.event.world.terrain.TerrainUnloadedEvent;
import outskirts.network.play.packet.SPacketTerrainData;
import outskirts.network.play.packet.SPacketTerrainUnload;
import outskirts.server.OutskirtsServer;
import outskirts.util.CopyOnIterateArrayList;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.registry.Registrable;
import outskirts.util.vector.Vector3f;
import outskirts.world.gen.TerrainGenerator;
import outskirts.world.terrain.Terrain;

import java.io.File;
import java.util.*;

/**
 * for implements Registrable for WorldServer, in Server, had many/onemore worlds are running/saves-exists.
 * to get/stores and keep good Scalability and Robustness, needs StringIdentity to identify.
 */
public class WorldServer extends World implements Registrable {

    private TerrainLoader terrainLoader = new TerrainLoader(this);
    private TerrainGenerator terrainGenerator = new TerrainGenerator();

    private File worldDirectory;

    // does this has Misleading/Confusion ..?
    private String registryID; // there is none Registry actually, mainly for Unique Id type

    public WorldServer(File worldDirectory) {
        setRegistryID(worldDirectory.getName());
        this.worldDirectory = worldDirectory;
    }

    @Override
    public void onTick() {

        // update chunk map
        updateTerrainMap();


        super.onTick();
    }

    private void updateTerrainMap() {
        // checks which chunks needs be load
        for (EntityPlayerMP player : getPlayers()) {
            int viewdistance = player.getViewDistance();
            for (int x = viewdistance;x >= -viewdistance;x-=Terrain.SIZE) { // more should be i++ ? i-- just for load of client-model-building order
                for (int z = viewdistance;z >= -viewdistance;z-=Terrain.SIZE) {
                    Terrain terrain = provideTerrain(player.getPosition().x+x, player.getPosition().z+z); // get or load
                    if (!terrain.listeningPlayers.contains(player)) {
                        terrain.listeningPlayers.add(player);
                        player.connection.sendPacket(new SPacketTerrainData(terrain));
                    }
                }
            }
        }

        // checks which chunks needs be unload
        List<Long> unloadQueue = new ArrayList<>(); // post-unload for avoid ConcurrentModificationException
        for (Terrain terrain : terrains.values()) {
            long terrpos = Terrain.posLong(terrain.x, terrain.z);

            Iterator<EntityPlayerMP> it = terrain.listeningPlayers.iterator();
            while (it.hasNext()) {
                EntityPlayerMP lsrplayer = it.next();
                if (!isTerrainInViewDistance(lsrplayer, terrain)) { // unvisible chunk of the player. out of viewDistance
                    it.remove();
                    lsrplayer.connection.sendPacket(new SPacketTerrainUnload(terrpos));
                }
            }
            if (terrain.listeningPlayers.isEmpty()) {
                unloadQueue.add(terrpos);
            }
        }
        unloadQueue.forEach(this::unloadTerrain); // post unload execution
    }
    private boolean isTerrainInViewDistance(EntityPlayerMP player, Terrain terrain) {
        return  Math.abs(Maths.floor(player.getPosition().x, Terrain.SIZE) - terrain.x) <= player.getViewDistance() &&
                Math.abs(Maths.floor(player.getPosition().z, Terrain.SIZE) - terrain.z) <= player.getViewDistance();
    }

    // is this a tmp getterfilter function?
    private List<EntityPlayerMP> getPlayers() {
        List<EntityPlayerMP> result = new ArrayList<>();
        for (Entity e : getEntities()) {
            if (e instanceof EntityPlayerMP)
                result.add((EntityPlayerMP)e);
        }
        return result;
    }

    @Override
    protected Terrain loadTerrain(int x, int z) {
        Terrain terrain = terrainLoader.loadTerrain(x, z);
        if (terrain == null) {
            terrain = terrainGenerator.generateTerrain(this, x, z);
        }
        return terrain;
    }

    @Override
    public Terrain unloadTerrain(float x, float z) {
        Terrain terrain = super.unloadTerrain(x, z);
        terrainLoader.saveTerrain(terrain);

        return terrain;
    }

    public File getWorldDirectory() {
        return worldDirectory;
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }
}
