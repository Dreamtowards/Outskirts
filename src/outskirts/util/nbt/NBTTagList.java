package outskirts.util.nbt;

import outskirts.util.Validate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class NBTTagList extends NBT implements Iterable<NBT> {

    private List<NBT> tagList = new ArrayList<>();

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(tagList.size());
        output.writeByte(getListType());

        for (NBT nbt : tagList) {
            nbt.write(output);
        }
    }

    @Override
    public void read(DataInput input) throws IOException {
        int size = input.readInt();
        byte listtype = input.readByte();

        for (int i = 0;i < size;i++) {
            NBT nbt = createNBT(listtype);
            nbt.read(input);

            add(nbt);
        }
    }

    @Override
    public byte type() {
        return LIST;
    }

    @Override
    public NBT copy() {
        NBTTagList result = new NBTTagList();
        for (NBT nbt : this.tagList) {
            result.add(nbt.copy());
        }
        return result;
    }

    @Override
    public String toString() {
        return tagList.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagList && ((NBTTagList) obj).tagList.equals(this.tagList);
    }

    @Override
    public int hashCode() {
        return type() ^ tagList.hashCode();
    }

    public byte getListType() {
        return tagList.isEmpty() ? END : tagList.get(0).type();
    }

    private void checkElement(NBT nbt) {
        Validate.notNull(nbt, "NBTTagList elements must be non-null");
        Validate.isTrue(tagList.isEmpty() || nbt.type() == getListType(), "Wrong element Type. NBTTagList elements type must be same.");
    }

    public void add(NBT nbt) {
        checkElement(nbt);

        tagList.add(nbt);
    }
    public void addByte(byte b) {
        add(new NBTTagByte(b));
    }
    public void addShort(short s) {
        add(new NBTTagShort(s));
    }
    public void addInt(int i) {
        add(new NBTTagInt(i));
    }
    public void addLong(int l) {
        add(new NBTTagLong(l));
    }
    public void addFloat(float f) {
        add(new NBTTagFloat(f));
    }
    public void addDouble(double d) {
        add(new NBTTagDouble(d));
    }
    public void addByteArray(byte[] d) {
        add(new NBTTagByteArray(d));
    }

    public void set(int index, NBT nbt) {
        checkElement(nbt);

        tagList.set(index, nbt);
    }

    public void remove(int index) {
        tagList.remove(index);
    }

    public NBT get(int index) {
        return tagList.get(index);
    }

    public int size() {
        return tagList.size();
    }

    public boolean isEmpty() {
        return tagList.isEmpty();
    }

    public boolean contains(NBT nbt) {
        return tagList.contains(nbt);
    }

    @Override
    public Iterator<NBT> iterator() {
        return tagList.iterator();
    }

    public byte getByte(int index) {
        return ((NBTTagByte)get(index)).getByte();
    }

    public short getShort(int index) {
        return ((NBTTagShort)get(index)).getShort();
    }

    public int getInt(int index) {
        return ((NBTTagInt)get(index)).getInt();
    }

    public long getLong(int index) {
        return ((NBTTagLong)get(index)).getLong();
    }

    public float getFloat(int index) {
        return ((NBTTagFloat)get(index)).getFloat();
    }

    public double getDouble(int index) {
        return ((NBTTagDouble)get(index)).getDouble();
    }

    public byte[] getByteArray(int index) {
        return ((NBTTagByteArray)get(index)).getByteArray();
    }

    public String getString(int index) {
        return ((NBTTagString)get(index)).getString();
    }

    public NBTTagList getListTag(int index) {
        return (NBTTagList)get(index);
    }

    public NBTTagCompound getCompoundTag(int index) {
        return (NBTTagCompound)get(index);
    }
}
