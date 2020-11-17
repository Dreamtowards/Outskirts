package outskirts.item.inventory;

import outskirts.item.stack.ItemStack;
import outskirts.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// should size changeable
/*
  ItemStack should not been null. because ItemStack is a container,
  its can change amount, metadata, even item type, while in one ItemStack object.
  its like a slot. this may cause custom-tive down, but flixable more.

  i.e. the ItemStack instance in Inventory::itemstacks is fixed.
  then we can just use one ItemStack to free set a lot, shink to empty, with out locate the ItemStack container.
 */
public class Inventory {

    private ItemStack[] itemstacks;

    public Inventory(int capacity) {
        this.itemstacks = CollectionUtils.fill(new ItemStack[capacity], ItemStack::new);
    }

    public boolean add(ItemStack itemstack) {
        for (ItemStack s : itemstacks) {
            if (s.empty()) {
                s.set(itemstack);
                return true;
            } else if (s.equalsItem(itemstack)){
                s.grow(itemstack.amount());
                return true;
            }
        }
        return false;
    }

    public ItemStack get(int index) {
        return itemstacks[index];
    }

    public List<ItemStack> getItemStacks() {
        return Collections.unmodifiableList(Arrays.asList(itemstacks));
    }
}
