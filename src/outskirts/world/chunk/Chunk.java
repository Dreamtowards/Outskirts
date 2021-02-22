package outskirts.world.chunk;

import outskirts.client.Outskirts;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.entity.Entity;
import outskirts.entity.item.EntityDropItem;
import outskirts.entity.item.EntityStaticMesh;
import outskirts.entity.player.EntityPlayer;
import outskirts.event.world.chunk.section.SectionLoadedEvent;
import outskirts.event.world.chunk.section.SectionUnloadedEvent;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.storage.Savable;
import outskirts.storage.dst.DArray;
import outskirts.storage.dst.DObject;
import outskirts.util.IOUtils;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static outskirts.util.logging.Log.LOGGER;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.aabb;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.event.Events.EVENT_BUS;
import static outskirts.util.Maths.INFINITY;
import static outskirts.util.vector.Vector3f.ZERO;

public class Chunk implements Savable {

    /**
     * SparseArray holds Y-Unlimited Sections.
     * the key:int is floor(ypos, 16).
     */
    private Map<Integer, Octree.Internal> sections = new HashMap<>();

    public final int x;
    public final int z;

    private World world;  // rf.

    public boolean populated = false;

    public Chunk(World world, int x, int z) {
        ChunkPos.validate(x, z);
        this.x = x;
        this.z = z;
        this.world = world;
    }

    /**
     * Get Section Root-Octree.
     */
    public Octree.Internal octree(float ypos) {
        int k = Maths.floor(ypos, 16);
        return sections.get(k);
    }

    /**
     * Init Section Root-Octree.
     */
    public void octree(float ypos, Octree node) {
        int k = Maths.floor(ypos, 16);
        Vector3f p = vec3(x, k, z);
        if (node == null) {
            // Unload.
            Octree pn = sections.remove(k);  assert pn != null : "Dosent exists.";
            EVENT_BUS.post(new SectionUnloadedEvent(this, p));
        } else {
            // Load
            Octree pn = sections.put(k, (Octree.Internal)node);  assert pn == null : "Cant Replace. looks no sense";
            EVENT_BUS.post(new SectionLoadedEvent(this, p));
        }
    }

    public Map<Integer, Octree.Internal> getOctrees() {
        return sections;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void onRead(DObject mp) throws IOException {

        DObject mpMetadata = mp.getDObject("metadata");
        populated = mpMetadata.getBoolean("populated");
        assert  x==mpMetadata.getInt("x") &&
                z==mpMetadata.getInt("z");


        DArray<DObject> mpEntities = mp.getDArray("entities");
        for (DObject mpEntity : mpEntities) {
            getWorld().addEntity(Entity.loadEntity(mpEntity));
        }


        InputStream bSections = mp.getByteArrayi("sections");
        int numSections = IOUtils.readShort(bSections) & 0xFFFF; // assert numSections >= 0;
        for (int i = 0; i < numSections; i++) {
            int yk = IOUtils.readShort(bSections);
            Octree rnode = Octree.readOctree(bSections, vec3(0), 16f);
            octree(yk, rnode);
        }
    }

    @Override
    public DObject onWrite(DObject mp) throws IOException {

        DObject mpMetadata = new DObject();
        mpMetadata.putInt("x", x);
        mpMetadata.putInt("z", z);
        mpMetadata.putLong("modify_time", System.currentTimeMillis());  // cuz. do not needs very accuracy.
        mpMetadata.putBoolean("populated", populated);
        mp.put("metadata", mpMetadata);


        DArray<DObject> lsEntities = new DArray<>();
        for (Entity entity : world.getEntitiesfc(getAABB(aabb()))) {
            if (!shouldStore(entity)) continue;
            DObject mpEntity = entity.onWrite(new DObject());
            lsEntities.add(mpEntity);
        }
        mp.put("entities", lsEntities);


        ByteArrayOutputStream bSections = new ByteArrayOutputStream();
        IOUtils.writeShort(bSections, (short)sections.size());  assert sections.size() < 65536;
        for (int yk : sections.keySet()) {                      assert Math.abs(yk) < 32768;
            IOUtils.writeShort(bSections, (short)yk);
            Octree.writeOctree(bSections, sections.get(yk));
        }
        mp.put("sections", bSections.toByteArray());

        return mp;
    }

    private static boolean shouldStore(Entity entity) {
        if (entity instanceof EntityPlayer || entity instanceof EntityStaticMesh || entity instanceof EntityDropItem)
            return false;
        return true;
    }

    private AABB getAABB(AABB dest) {
        return dest.set(x,-INFINITY,z, x+16,INFINITY,z+16);
    }

}
