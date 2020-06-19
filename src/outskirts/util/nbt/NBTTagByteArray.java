package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBT {

    private byte[] data;

    public NBTTagByteArray() {}

    public NBTTagByteArray(byte[] data) {
        this.data = data;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.data.length);
        output.write(this.data);
    }

    @Override
    public void read(DataInput input) throws IOException {
        int length = input.readInt();
        this.data = new byte[length];
        input.readFully(this.data);
    }

    @Override
    public byte type() {
        return BYTE_ARRAY;
    }

    @Override
    public NBTTagByteArray copy() {
        return new NBTTagByteArray(Arrays.copyOf(this.data, this.data.length));
    }

    @Override
    public String toString() {
        return "B" + Arrays.toString(this.data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagByteArray && Arrays.equals(((NBTTagByteArray) obj).data, this.data);
    }

    @Override
    public int hashCode() {
        return type() ^ Arrays.hashCode(this.data);
    }

    public byte[] getByteArray() {
        return data;
    }
}
