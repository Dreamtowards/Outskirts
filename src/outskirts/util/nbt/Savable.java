package outskirts.util.nbt;

public interface Savable {

    void readNBT(NBTTagCompound tagCompound);

    NBTTagCompound writeNBT(NBTTagCompound tagCompound);

}
