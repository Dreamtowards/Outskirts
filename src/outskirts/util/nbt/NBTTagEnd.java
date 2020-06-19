package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBT {

    public NBTTagEnd() {}

    @Override
    public void write(DataOutput output) throws IOException { }

    @Override
    public void read(DataInput input) throws IOException { }

    @Override
    public byte type() {
        return END;
    }

    @Override
    public NBTTagEnd copy() {
        return new NBTTagEnd();
    }

    @Override
    public String toString() {
        return "END";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagEnd;
    }

    @Override
    public int hashCode() {
        return type();
    }
}
