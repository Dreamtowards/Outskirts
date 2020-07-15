package outskirts.world;

import outskirts.entity.Entity;
import outskirts.entity.player.EntityPlayer;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.event.Event;
import outskirts.event.Events;
import outskirts.server.OutskirtsServer;
import outskirts.storage.Savable;
import outskirts.util.CopyOnIterateArrayList;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.registry.Registrable;
import outskirts.util.vector.Vector3f;

import java.io.File;
import java.util.*;

/**
 * for implements Registrable for WorldServer, in Server, had many/onemore worlds are running/saves-exists.
 * to get/stores and keep good Scalability and Robustness, needs StringIdentity to identify.
 */
public class WorldServer extends World implements Registrable {

//    private TerrainLoader terrainLoader = new TerrainLoader(this);
//    private TerrainGenerator terrainGenerator = new TerrainGenerator();

    private File worldDirectory;

    // does this has Misleading/Confusion ..?
    private String registryID; // there is none Registry actually, mainly for Unique Id type

    public WorldServer(File worldDirectory) {
        setRegistryID(worldDirectory.getName());
        this.worldDirectory = worldDirectory;
    }

    public File getWorldDirectory() {
        return worldDirectory;
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }

}
