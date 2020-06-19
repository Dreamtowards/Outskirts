package outskirts.block;

import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.init.BlockTextures;
import outskirts.world.World;
import outskirts.world.chunk.Octree;

import java.util.List;

public class BlockDirt extends Block {

    public BlockDirt() {
        setRegistryID("dirt");
    }

    @Override
    public void getVertexData(World world, Octree octree, List<Float> positions, List<Float> textureCoords, List<Float> normals) {

        ChunkModelGenerator.putCube(
                world,
                positions, textureCoords, normals,
                octree.blockPos(),
                octree.depth(),
                octree.size(),
                BlockTextures.DIRT.OFFSET,
                BlockTextures.DIRT.SCALE
        );
    }
}
