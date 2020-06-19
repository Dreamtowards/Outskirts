package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBT {

    private int data;

    public NBTTagInt() {}

    public NBTTagInt(int data) {
        this.data = data;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.data);
    }

    @Override
    public void read(DataInput input) throws IOException {
        this.data = input.readInt();
    }

    @Override
    public byte type() {
        return INT;
    }

    @Override
    public NBTTagInt copy() {
        return new NBTTagInt(this.data);
    }

    @Override
    public String toString() {
        return this.data + "i";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagInt && ((NBTTagInt) obj).data == this.data;
    }

    @Override
    public int hashCode() {
        return type() ^ this.data;
    }

    public int getInt() {
        return data;
    }
}
