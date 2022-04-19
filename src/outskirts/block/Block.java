package outskirts.block;

import outskirts.client.render.VertexBuffer;
import outskirts.util.Side;
import outskirts.util.SideOnly;
import outskirts.util.vector.Vector3f;

public abstract class Block {


    @SideOnly(Side.CLIENT)
    public void getVertexData(VertexBuffer vbuf, Vector3f chunkpos, int rx, int ry, int rz) {
        throw new UnsupportedOperationException();
    }

}
