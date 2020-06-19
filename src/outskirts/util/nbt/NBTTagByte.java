package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBT {

    private byte data;

    public NBTTagByte() {}

    public NBTTagByte(byte data) {
        this.data = data;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(this.data);
    }

    @Override
    public void read(DataInput input) throws IOException {
        this.data = input.readByte();
    }

    @Override
    public byte type() {
        return BYTE;
    }

    @Override
    public NBTTagByte copy() {
        return new NBTTagByte(this.data);
    }

    @Override
    public String toString() {
        return this.data + "b";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagByte && ((NBTTagByte) obj).data == this.data;
    }

    @Override
    public int hashCode() {
        return type() ^ this.data;
    }

    public byte getByte() {
        return data;
    }
}
