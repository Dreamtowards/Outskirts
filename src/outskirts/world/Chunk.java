package outskirts.world;

import outskirts.entity.EntityStaticMesh;

public class Chunk {

    public byte[] blocks = new byte[65536];  // 16*16*256

    public final int x;
    public final int z;

    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;

        proxyEntity.getPosition().set(x, 0, z);
    }

    private int index(int x, int y, int z) {
        return ((x & 0xF) << 12) | ((y & 0xFF) << 4) | (z & 0xF);
    }

    public byte getAt(int x, int y, int z) {
//        if (x<0||x>=16 || y<0||y>=256 || z<0||z>=16)  // when test for neiberghts
//            return 0;
        return blocks[index(x, y, z)];
    }

    public void setAt(int x, int y, int z, byte b) {
        blocks[index(x, y, z)] = b;
    }


    public boolean markedRebuildModel = false;
    public EntityStaticMesh proxyEntity = new EntityStaticMesh();
}
