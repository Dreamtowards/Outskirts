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
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.HashMap;
import java.util.Map;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.aabb;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.event.Events.EVENT_BUS;

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
            sections.remove(k);
            EVENT_BUS.post(new SectionUnloadedEvent(this, p));
        } else {
            // Load
            Octree prev = sections.put(k, (Octree.Internal)node);
            assert prev == null : "Cant Replace. looks no sense";
            EVENT_BUS.post(new SectionLoadedEvent(this, p));
            world.markOctreesModify(aabb(p, 16));
        }
    }

    public Map<Integer, Octree.Internal> getOctrees() {
        return sections;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void onRead(DObject mp) {

        DObject mpMetadata = mp.getDObject("metadata");
        populated = mpMetadata.getBoolean("populated");


        DArray<DObject> mpEntities = mp.getDArray("entities");
        for (DObject mpEntity : mpEntities) {
            getWorld().addEntity(Entity.loadEntity(mpEntity));
        }


//        DObject mpTerrain = mp.getDObject("terrain"); {
//            DArray<String> lsBlocks = mpTerrain.getDArray("blocks");
//            int i = 0;
//            for (int x = 0; x < 16; x++) {
//                for (int y = 0; y < 32; y++) {
//                    for (int z = 0; z < 16; z++) {
//                        String s = lsBlocks.getString(i);
//                        if (!s.isEmpty()) {
//                            setBlock(x, y, z, Block.REGISTRY.get(s));
//                        }
//                        i++;
//                    }
//                }
//            }
//        }

    }

    @Override
    public DObject onWrite(DObject mp) {

        {
            DObject mpMetadata = new DObject();
            mpMetadata.put("x", x);
            mpMetadata.put("z", z);
            mpMetadata.put("modify_time", Outskirts.getSystemTime());
            mpMetadata.putBoolean("populated", populated);
            mp.put("metadata", mpMetadata);
        }

        {
            DArray<DObject> lsEntities = new DArray<>();
            AABB chunkAabb = new AABB(x, 0, z, x+16, 256, z+16);
            for (Entity entity : world.getEntitiesfc(chunkAabb)) {
                if (entity instanceof EntityPlayer || entity instanceof EntityStaticMesh || entity instanceof EntityDropItem)
                    continue;
                DObject mpEntity = new DObject();
                entity.onWrite(mpEntity);
                lsEntities.add(mpEntity);
            }
            mp.put("entities", lsEntities);
        }

        {
            DObject mpTerrain = new DObject();
//            DArray lsBlocks = new DArray(); {
//                for (int x = 0;x < 16;x++) {
//                    for (int y = 0;y < 32;y++) {
//                        for (int z = 0;z < 16;z++) {
//                            Block b = getBlock(x,y,z);
//                            lsBlocks.add(b==null ? "" : b.getRegistryID());
//                        }
//                    }
//                }
//            } mpTerrain.put("blocks", lsBlocks);
            mp.put("terrain", mpTerrain);
        }

        return mp;
    }

}
