package outskirts.block;

import outskirts.client.material.TextureAtlas;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.client.render.chunk.PolygoniseMCGenerator;
import outskirts.entity.Entity;
import outskirts.entity.EntityDropItem;
import outskirts.item.Item;
import outskirts.item.stack.ItemStack;
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

    private float hardness = 1;


    @SideOnly(Side.CLIENT)
    public void getVertexData(World world, Vector3f blockpos, VertexBuffer vbuf) {

//        ChunkModelGenerator.putCubeVtx(vbuf, blockpos, world, theTxFrag);

//        PolygoniseMCGenerator.putPolygonise(vbuf, world, blockpos);
    }

    public void onDestroyed(World world, Vector3f blockpos) {

        EntityDropItem dropItem = new EntityDropItem(new ItemStack(Item.ofBlock(this)));

        dropItem.position().set(blockpos).add(0.5f, 0.5f, 0.5f);
        dropItem.rigidbody().getLinearVelocity().add((float)Math.random()*2f, 2f, (float)Math.random()*2f);

        world.addEntity(dropItem);
    }

    public final void setTranslucent(boolean translucent) {
        this.translucent = translucent;
    }
    public final boolean isTranslucent() {
        return translucent;
    }

    public final void setHardness(float hardness) {
        this.hardness = hardness;
    }
    public final float getHardness() {
        return hardness;
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }
}
