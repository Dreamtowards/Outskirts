package outskirts.util.nbt;

import outskirts.util.vector.Vector3f;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class NBTTagCompound extends NBT {

    private Map<String, NBT> tagMap = new HashMap<>();

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(tagMap.size());

        for (Map.Entry<String, NBT> entry : tagMap.entrySet()) {
            output.writeUTF(entry.getKey());

            NBT nbt = entry.getValue();
            output.writeByte(nbt.type());
            nbt.write(output);
        }
    }

    @Override
    public void read(DataInput input) throws IOException {
        int size = input.readInt();

        for (int i = 0;i < size;i++) {
            String key = input.readUTF();

            byte type = input.readByte();
            NBT nbt = createNBT(type);
            nbt.read(input);

            this.setTag(key, nbt);
        }
    }

    @Override
    public byte type() {
        return COMPOUND;
    }

    @Override
    public NBT copy() {
        NBTTagCompound tagCompound = new NBTTagCompound();

        for (Map.Entry<String, NBT> entry : tagMap.entrySet()) {
            tagCompound.setTag(entry.getKey(), entry.getValue().copy());
        }

        return tagCompound;
    }

    @Override
    public String toString() {
        return this.tagMap.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagCompound && ((NBTTagCompound) obj).tagMap.equals(this.tagMap);
    }

    @Override
    public int hashCode() {
        return type() ^ this.tagMap.hashCode();
    }

    //TODO: setTag() or set()
    public void setTag(String key, NBT nbt) {
        tagMap.put(Objects.requireNonNull(key), Objects.requireNonNull(nbt, "NBTTagCompound entry must be non-null"));
    }

    public NBT getTag(String key) {
        return tagMap.get(key);
    }

    public void removeTag(String key) {
        tagMap.remove(key);
    }

    public boolean isEmpty() {
        return tagMap.isEmpty();
    }

    public int size() {
        return tagMap.size();
    }

    public Set<String> keySet() {
        return tagMap.keySet();
    }

    public Set<Map.Entry<String, NBT>> entrySet() {
        return tagMap.entrySet();
    }

    public boolean hasKey(String key) {
        return tagMap.containsKey(key);
    }


    public byte getByte(String key) {
        return ((NBTTagByte)getTag(key)).getByte();
    }
    public void setByte(String key, byte value) {
        setTag(key, new NBTTagByte(value));
    }

    public short getShort(String key) {
        return ((NBTTagShort)getTag(key)).getShort();
    }
    public void setShort(String key, short value) {
        setTag(key, new NBTTagShort(value));
    }

    public int getInt(String key) {
        return ((NBTTagInt)getTag(key)).getInt();
    }
    public void setInt(String key, int value) {
        setTag(key, new NBTTagInt(value));
    }

    public long getLong(String key) {
        return ((NBTTagLong)getTag(key)).getLong();
    }
    public void setLong(String key, long value) {
        setTag(key, new NBTTagLong(value));
    }

    public float getFloat(String key) {
        return ((NBTTagFloat)getTag(key)).getFloat();
    }
    public void setFloat(String key, float value) {
        setTag(key, new NBTTagFloat(value));
    }

    public double getDouble(String key) {
        return ((NBTTagDouble)getTag(key)).getDouble();
    }
    public void setDouble(String key, double value) {
        setTag(key, new NBTTagDouble(value));
    }

    public byte[] getByteArray(String key) {
        return ((NBTTagByteArray)getTag(key)).getByteArray();
    }
    public void setByteArray(String key, byte[] value) {
        setTag(key, new NBTTagByteArray(value));
    }

    public String getString(String key) {
        return ((NBTTagString)getTag(key)).getString();
    }
    public void setString(String key, String value) {
        setTag(key, new NBTTagString(value));
    }

    public NBTTagList getListTag(String key) {
        return (NBTTagList)getTag(key);
    }
    public void setListTag(String key, NBTTagList value) {
        setTag(key, value);
    }

    public NBTTagCompound getCompoundTag(String key) {
        return (NBTTagCompound)getTag(key);
    }
    public void setCompoundTag(String key, NBTTagCompound value) {
        setTag(key, value);
    }




    public boolean getBoolean(String key) {
        return !(getByte(key) == 0);
    }
    public void setBoolean(String key, boolean value) {
        setByte(key, (byte)(value ? 1 : 0));
    }

    public Vector3f getVector3f(String key, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        NBTTagList tagList = getListTag(key);
        dest.x = tagList.getFloat(0);
        dest.y = tagList.getFloat(1);
        dest.z = tagList.getFloat(2);
        return dest;
    }
    public void setVector3f(String key, Vector3f value) {
        NBTTagList tagList = new NBTTagList();
        tagList.addFloat(value.x);
        tagList.addFloat(value.y);
        tagList.addFloat(value.z);
        setListTag(key, tagList);
    }

}
