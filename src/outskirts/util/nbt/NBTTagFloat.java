package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBT {

    private float data;

    public NBTTagFloat() {}

    public NBTTagFloat(float data) {
        this.data = data;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeFloat(this.data);
    }

    @Override
    public void read(DataInput input) throws IOException {
        this.data = input.readFloat();
    }

    @Override
    public byte type() {
        return FLOAT;
    }

    @Override
    public NBTTagFloat copy() {
        return new NBTTagFloat(this.data);
    }

    @Override
    public String toString() {
        return this.data + "f";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagFloat && ((NBTTagFloat) obj).data == this.data;
    }

    @Override
    public int hashCode() {
        return type() ^ Float.floatToIntBits(this.data);
    }

    public float getFloat() {
        return data;
    }
}
