package outskirts.item.stack;

import outskirts.item.Item;

import java.util.Objects;

public final class ItemStack {

    private Item item;  // just for the feel safity.
    private int stacksize;

    public ItemStack(Item item, int amount) {
        setItem(item);
        setAmount(amount);
    }
    public ItemStack(Item item) {
        this(item, 1);
    }

    public ItemStack() {
        this(null, 0);
    }

    public void set(ItemStack itemstack) {
        setItem(itemstack.getItem());
        setAmount(itemstack.amount());
    }

    public void setAmount(int amount) {
        this.stacksize = amount;
    }
    public int amount() {
        return stacksize;
    }

    public boolean empty() {
        return amount()==0 || getItem()==null;
    }

    public void grow(int n) {
        setAmount(amount() + n);
    }
    public void shrink(int n) {
        grow(-n);
    }

    public void setItem(Item item) {
        this.item = item;
    }
    public Item getItem() {
        return item;
    }

    public boolean equalsItem(ItemStack itemstack) {
        if (itemstack == null) return false;
        return getItem() == itemstack.getItem(); // equals().?
    }
}
