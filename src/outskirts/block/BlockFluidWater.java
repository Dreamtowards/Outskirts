package outskirts.block;

import outskirts.client.render.VertexBuffer;
import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

public class BlockFluidWater extends Block {

    public BlockFluidWater() {
        setRegistryID("water");
    }

    @Override
    public void getVertexData(World world, Vector3f blockpos, VertexBuffer vbuf) {

        ChunkModelGenerator.putCubeFace(vbuf, blockpos, 2, theTxFrag, Matrix3f.IDENTITY, new Vector3f(0, -0.2f, 0));
    }
}
