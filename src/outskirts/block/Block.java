package outskirts.block;

import outskirts.client.material.TextureAtlas;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.util.Side;
import outskirts.util.SideOnly;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

public abstract class Block implements Registrable {

    public static Registry<Block> REGISTRY = new Registry.RegistrableRegistry<>();

    public static TextureAtlas TEXTURE_ATLAS = new TextureAtlas();

    private String registryID;

    public TextureAtlas.Fragment theTxFrag;

    private boolean translucent;


    @SideOnly(Side.CLIENT)
    public void getVertexData(World world, Vector3f blockpos, VertexBuffer vbuf) {

        ChunkModelGenerator.putCubeVtx(vbuf, blockpos, world, theTxFrag);
    }

    public void setTranslucent(boolean translucent) {
        this.translucent = translucent;
    }
    public boolean isTranslucent() {
        return translucent;
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }
}
