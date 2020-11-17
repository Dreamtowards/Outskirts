package outskirts.entity;

import outskirts.block.Block;
import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.VertexBuffer;
import outskirts.entity.player.EntityPlayer;
import outskirts.init.ex.Models;
import outskirts.item.ItemBlock;
import outskirts.item.stack.ItemStack;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.physics.collision.shapes.convex.ConvexHullShape;
import outskirts.physics.extras.quickhull.QuickHull;
import outskirts.util.Ref;
import outskirts.util.mx.VertexUtils;
import outskirts.util.vector.Vector3f;

public class EntityDropItem extends Entity {

    private ItemStack itemstack;

    public EntityDropItem(ItemStack itemstack) {
        this.itemstack = itemstack;

        if (itemstack.getItem() instanceof ItemBlock) {
            Block b = ((ItemBlock)itemstack.getItem()).getBlock();

            VertexBuffer vbuf = new VertexBuffer();
            b.getVertexData(Outskirts.getWorld(), new Vector3f(0, -10, 0), vbuf);

            Ref<float[]> prf = Ref.wrap();
            setModel(Loader.loadModelT(vbuf, pos ->
                    prf.value=VertexUtils.scale(VertexUtils.centeraabb(pos), 0.32f)
            ));

            rigidbody().setCollisionShape(new ConvexHullShape(QuickHull.quickHull(prf.value)));
            getMaterial().setDiffuseMap(Block.TEXTURE_ATLAS.getAtlasTexture());
        } else {
            setModel(Models.GEO_CUBE);
            rigidbody().setCollisionShape(new BoxShape(1,1,1));
        }
    }

    @Override
    public void onTick() {

        for (Entity entity : getWorld().getEntities(new AABB(rigidbody().getAABB()).grow(2,2,2))) {
            if (entity instanceof EntityPlayer) {
                Vector3f posdiff = Vector3f.sub(entity.position(), position(), null);
                rigidbody().getLinearVelocity().add(posdiff);

                if (posdiff.length() < 1.0f) {
                    if (((EntityPlayer)entity).getBackpackInventory().add(getItemStack())) {
                        getWorld().removeEntity(this);
                        break;
                    }
                }
            }
        }

        super.onTick();
    }

    public ItemStack getItemStack() {
        return itemstack;
    }
}
