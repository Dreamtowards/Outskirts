package outskirts.world.chunk;

import java.util.Objects;

public final class ChunkPos {

    public final int x;
    public final int z;

    private ChunkPos(int x, int z) {
        assert x%16==0 && z%16==0 : String.format("x: %s, z: %s", x, z);
        this.x = x;
        this.z = z;
    }

    public static ChunkPos of(Chunk chunk) {
        return ChunkPos.of(chunk.x, chunk.z);
    }
    public static ChunkPos of(long posLong) {
        return ChunkPos.of((int)(posLong >>> 32), (int)posLong);
    }
    public static ChunkPos of(int xPos, int zPos) {
        return new ChunkPos(xPos, zPos);
    }

    public static long asLong(int x, int z) {
        assert x%16==0 && z%16==0;
        return ((x & 0xFFFFFFFFL) << 32) | (z & 0xFFFFFFFFL);
    }

    @Override
    public String toString() {
        return "ChunkPos("+x+", "+z+")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ChunkPos && x == ((ChunkPos)o).x && z == ((ChunkPos)o).z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
