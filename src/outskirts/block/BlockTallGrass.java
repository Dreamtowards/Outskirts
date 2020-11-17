package outskirts.block;

import outskirts.client.render.VertexBuffer;
import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

public class BlockTallGrass extends Block {

    public BlockTallGrass() {
        setRegistryID("tall_grass");
        setTranslucent(true);

        setHardness(0f);
    }

    @Override
    public void getVertexData(World world, Vector3f blockpos, VertexBuffer vbuf) {


        ChunkModelGenerator.putTallgrassCrossFaces(vbuf, blockpos, theTxFrag);
    }
}
