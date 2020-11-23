package outskirts.world.chunk;

import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;

import java.util.Arrays;
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
    public static ChunkPos of(float xPos, float zPos) {
        return new ChunkPos(Maths.floor(xPos, 16), Maths.floor(zPos, 16));
    }
    public static ChunkPos of(Vector3f p) {
        return ChunkPos.of(p.x, p.z);
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
        return 31 * x ^ z;
    }
}
