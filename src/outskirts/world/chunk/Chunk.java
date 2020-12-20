package outskirts.world.chunk;

import outskirts.block.Block;
import outskirts.client.Outskirts;
import outskirts.entity.Entity;
import outskirts.entity.EntityDropItem;
import outskirts.entity.EntityTerrainMesh;
import outskirts.entity.player.EntityPlayer;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.storage.Savable;
import outskirts.storage.dst.DArray;
import outskirts.storage.dst.DObject;
import outskirts.util.CollectionUtils;
import outskirts.world.World;

public class Chunk implements Savable {

    private Section[] sections = CollectionUtils.fill(new Section[16], Section::new);  // 16*16*256

    public final int x;
    public final int z;

    private World world;  // rf.

    public boolean populated = false;

    public Chunk(World world, int x, int z) {
        ChunkPos.validate(x, z);
        this.x = x;
        this.z = z;
        this.world = world;

        proxyEntity.position().set(x, 0, z);
    }

    /**
     * @param x,y,z [0,15], [0,255], [0,15] rel in the chunk.
     */
    public Block getBlock(int x, int y, int z) {
        return sections[y >> 4].get(x, y % 16, z);
    }

    public void setBlock(int x, int y, int z, Block b) {
        sections[y >> 4].set(x, y % 16, z, b);
    }


    public boolean markedRebuildModel = false;
    public EntityTerrainMesh proxyEntity = new EntityTerrainMesh();

    public World getWorld() {
        return world;
    }

    @Override
    public void onRead(DObject mp) {

        DObject mpMetadata = mp.getDObject("metadata"); {
            populated = mpMetadata.getBoolean("populated");
        }

        DArray<DObject> mpEntities = mp.getDArray("entities"); {
            for (DObject mpEntity : mpEntities) {
                Entity entity = Entity.loadEntity(mpEntity);
                getWorld().addEntity(entity);
            }
        }

        DObject mpTerrain = mp.getDObject("terrain"); {
            DArray lsBlocks = mpTerrain.getDArray("blocks"); {
                int i = 0;
                for (int x = 0;x < 16;x++) {
                    for (int y = 0;y < 32;y++) {
                        for (int z = 0;z < 16;z++) {
                            String s = lsBlocks.getString(i);
                            if (!s.isEmpty()) {
                                setBlock(x,y,z, Block.REGISTRY.get(s));
                            }
                            i++;
                        }
                    }
                }
                markedRebuildModel=true;
            }
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
                if (entity instanceof EntityPlayer || entity instanceof EntityTerrainMesh || entity instanceof EntityDropItem)
                    continue;
                DObject mpEntity = new DObject();
                entity.onWrite(mpEntity);
                lsEntities.add(mpEntity);
            }
        } mp.put("entities", lsEntities);

        DObject mpTerrain = new DObject(); {
            DArray lsBlocks = new DArray(); {
                for (int x = 0;x < 16;x++) {
                    for (int y = 0;y < 32;y++) {
                        for (int z = 0;z < 16;z++) {
                            Block b = getBlock(x,y,z);
                            lsBlocks.add(b==null ? "" : b.getRegistryID());
                        }
                    }
                }
            } mpTerrain.put("blocks", lsBlocks);

        } mp.put("terrain", mpTerrain);

        return mp;
    }


    public static class Section {

        private Block[] blocks = new Block[4096];

        private int index(int x, int y, int z) {
            return ((x & 0xF) << 8) | ((y & 0xF) << 4) | (z & 0xF);
        }

        public Block get(int x, int y, int z) {
            return blocks[index(x, y, z)];
        }

        public void set(int x, int y, int z, Block b) {
            blocks[index(x, y, z)] = b;
        }
    }
}
