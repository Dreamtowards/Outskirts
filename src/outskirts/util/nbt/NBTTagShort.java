package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBT {

    private short data;

    public NBTTagShort() {}

    public NBTTagShort(short data) {
        this.data = data;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeShort(this.data);
    }

    @Override
    public void read(DataInput input) throws IOException {
        this.data = input.readShort();
    }

    @Override
    public byte type() {
        return SHORT;
    }

    @Override
    public NBTTagShort copy() {
        return new NBTTagShort(this.data);
    }

    @Override
    public String toString() {
        return this.data + "s";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagShort && ((NBTTagShort) obj).data == this.data;
    }

    @Override
    public int hashCode() {
        return type() ^ this.data;
    }

    public short getShort() {
        return data;
    }
}
