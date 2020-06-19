package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBT {

    private String data;

    public NBTTagString() {}

    public NBTTagString(String data) {
        this.data = data;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.data);
    }

    @Override
    public void read(DataInput input) throws IOException {
        this.data = input.readUTF();
    }

    @Override
    public byte type() {
        return STRING;
    }

    @Override
    public NBT copy() {
        return new NBTTagString(this.data);
    }

    @Override
    public String toString() {
        return this.data;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NBTTagString && ((NBTTagString) obj).data.equals(this.data);
    }

    @Override
    public int hashCode() {
        return type() ^ this.data.hashCode();
    }

    public String getString() {
        return data;
    }
}
