package outskirts.world.chunk;

import outskirts.client.Outskirts;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.entity.Entity;
import outskirts.entity.item.EntityDropItem;
import outskirts.entity.item.EntityStaticMesh;
import outskirts.entity.player.EntityPlayer;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.storage.Savable;
import outskirts.storage.dst.DArray;
import outskirts.storage.dst.DObject;
import outskirts.world.World;

import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;

public class Chunk implements Savable {

    private Octree.Internal[] octrees = new Octree.Internal[16];

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



    private int octidx(float ypos) {
        if (ypos >= 256 || ypos < 0) return -1;
        return (int)(ypos/16f);
    }
    public Octree.Internal octree(float ypos) {
        int i = octidx(ypos);
        if (i==-1) return null;
        return octrees[i];
    }
    public Octree.Internal octree(float ypos, Octree node) {
        int i = octidx(ypos);
        assert node.isInternal();
        assert i != -1;
        return octrees[i] = (Octree.Internal)node;
    }


    public World getWorld() {
        return world;
    }

    @Override
    public void onRead(DObject mp) {

        {   DObject mpMetadata = mp.getDObject("metadata");
            populated = mpMetadata.getBoolean("populated");
        }

        {   DArray<DObject> mpEntities = mp.getDArray("entities");
            for (DObject mpEntity : mpEntities) {
                Entity entity = Entity.loadEntity(mpEntity);
                getWorld().addEntity(entity);
            }
        }

        {   DObject mpTerrain = mp.getDObject("terrain");
//            {   DArray lsBlocks = mpTerrain.getDArray("blocks");
//                int i = 0;
//                for (int x = 0;x < 16;x++) {
//                    for (int y = 0;y < 32;y++) {
//                        for (int z = 0;z < 16;z++) {
//                            String s = lsBlocks.getString(i);
//                            if (!s.isEmpty()) {
//                                setBlock(x,y,z, Block.REGISTRY.get(s));
//                            }
//                            i++;
//                        }
//                    }
//                }
//                markedRebuildModel=true;
//            }
        }

    }

    @Override
    public DObject onWrite(DObject mp) {

        DObject mpMetadata = new DObject(); {
            mpMetadata.put("x", x);
            mpMetadata.put("z", z);
//            mpMetadata.put("create_time", 0);
            mpMetadata.put("modify_time", Outskirts.getSystemTime());
            mpMetadata.putBoolean("populated", populated);
        } mp.put("metadata", mpMetadata);

        DArray lsEntities = new DArray(); {
            AABB chunkAabb = new AABB(x, 0, z, x+16, 256, z+16);
            for (Entity entity : world.getEntities(chunkAabb)) {
                if (entity instanceof EntityPlayer || entity instanceof EntityStaticMesh || entity instanceof EntityDropItem)
                    continue;
                DObject mpEntity = new DObject();
                entity.onWrite(mpEntity);
                lsEntities.add(mpEntity);
            }
        } mp.put("entities", lsEntities);

        DObject mpTerrain = new DObject(); {
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
        } mp.put("terrain", mpTerrain);

        return mp;
    }

}
