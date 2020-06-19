package outskirts.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.NoSuchElementException;

public abstract class NBT {

    static final byte VERSION = 1;

    public static final String[] TYPES = {"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE_ARRAY", "STRING", "LIST", "COMPOUND"};

    public static final byte END = 0;
    public static final byte BYTE = 1;
    public static final byte SHORT = 2;
    public static final byte INT = 3;
    public static final byte LONG = 4;
    public static final byte FLOAT = 5;
    public static final byte DOUBLE = 6;
    public static final byte BYTE_ARRAY = 7;
    public static final byte STRING = 8;
    public static final byte LIST = 9;
    public static final byte COMPOUND = 10;

    public abstract void write(DataOutput output) throws IOException;

    public abstract void read(DataInput input) throws IOException;

    public abstract byte type();

    public abstract NBT copy();

    public abstract String toString();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();



    public static NBT createNBT(byte type) {
        switch (type) {
            case END:
                return new NBTTagEnd();
            case BYTE:
                return new NBTTagByte();
            case SHORT:
                return new NBTTagShort();
            case INT:
                return new NBTTagInt();
            case LONG:
                return new NBTTagLong();
            case FLOAT:
                return new NBTTagFloat();
            case DOUBLE:
                return new NBTTagDouble();
            case BYTE_ARRAY:
                return new NBTTagByteArray();
            case STRING:
                return new NBTTagString();
            case LIST:
                return new NBTTagList();
            case COMPOUND:
                return new NBTTagCompound();
            default:
                throw new NoSuchElementException("Unknown NBT type: " + type);
        }
    }
}
