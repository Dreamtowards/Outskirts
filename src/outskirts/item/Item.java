package outskirts.item;

import outskirts.block.Block;
import outskirts.client.gui.Gui;
import outskirts.item.stack.ItemStack;
import outskirts.util.Colors;
import outskirts.util.Side;
import outskirts.util.SideOnly;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.world.World;

public abstract class Item implements Registrable {

    public static final Registry<Item> REGISTRY = new Registry<>();

    private String registryID;



    @SideOnly(Side.CLIENT)
    public void drawItem(float x, float y, float width, float height) {

        Gui.drawString(getRegistryID(), x, y, Colors.WHITE);
    }

    public boolean onItemUse(World world, ItemStack itemstack) {

        return false;
    }


    public static Item ofBlock(Block block) {
        return REGISTRY.get(block.getRegistryID());
    }


    @Override
    public String getRegistryID() {
        return registryID;
    }
}
