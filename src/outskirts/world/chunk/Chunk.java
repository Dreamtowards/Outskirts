package outskirts.world.chunk;

import outskirts.block.Block;
import outskirts.entity.EntityStaticMesh;
import outskirts.util.CollectionUtils;

public class Chunk {

    private Section[] sections = CollectionUtils.fill(new Section[16], Section::new);  // 16*16*256

    public final int x;
    public final int z;

    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;

        proxyEntity.getPosition().set(x, 0, z);
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
    public EntityStaticMesh proxyEntity = new EntityStaticMesh();


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
