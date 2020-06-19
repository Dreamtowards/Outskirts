package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBT {

    private double data;

    public NBTTagDouble() {}

    public NBTTagDouble(double data) {
        this.data = data;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeDouble(this.data);
    }

    @Override
    public void read(DataInput input) throws IOException {
        this.data = input.readDouble();
    }

    @Override
    public byte type() {
        return DOUBLE;
    }

    @Override
    public NBTTagDouble copy() {
        return new NBTTagDouble(this.data);
    }

    @Override
    public String toString() {
        return this.data + "d";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagDouble && ((NBTTagDouble) obj).data == this.data;
    }

    @Override
    public int hashCode() {
        return type() ^ (int) Double.doubleToLongBits(this.data);
    }

    public double getDouble() {
        return data;
    }
}
