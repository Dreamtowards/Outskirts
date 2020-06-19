package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBT {

    private long data;

    public NBTTagLong() {}

    public NBTTagLong(long data) {
        this.data = data;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(this.data);
    }

    @Override
    public void read(DataInput input) throws IOException {
        this.data = input.readLong();
    }

    @Override
    public byte type() {
        return LONG;
    }

    @Override
    public NBTTagLong copy() {
        return new NBTTagLong(this.data);
    }

    @Override
    public String toString() {
        return this.data + "L";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagLong && ((NBTTagLong) obj).data == this.data;
    }

    @Override
    public int hashCode() {
        return type() ^ (int)this.data;
    }

    public long getLong() {
        return data;
    }
}
