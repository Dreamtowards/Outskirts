package outskirts.item;

import outskirts.block.Block;
import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.item.stack.ItemStack;
import outskirts.world.World;

import java.util.Objects;

public class ItemBlock extends Item {

    private Block block;

    public ItemBlock(Block block) {
        setRegistryID(block.getRegistryID()); // todo: TMP.? its maybe collide id.
        this.block = block;
    }

    @Override
    public void drawItem(float x, float y, float width, float height) {

        Gui.drawTexture(Block.TEXTURE_ATLAS.getAtlasTexture(), x, y, width, height,
                block.theTxFrag.OFFSET.x,
                1f-block.theTxFrag.OFFSET.y-block.theTxFrag.SCALE.y,
                block.theTxFrag.SCALE.x,
                block.theTxFrag.SCALE.y);
    }

    @Override
    public boolean onItemUse(World world, ItemStack itemstack) {

//        if (ClientSettings.uSetBlockAtFocus(getBlock(), -1)) {
//            itemstack.shrink(1);
//            return true;
//        } else return false;
        return false;
    }

    public Block getBlock() {
        return block;
    }
}
