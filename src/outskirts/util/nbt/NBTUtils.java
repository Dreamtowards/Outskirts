package outskirts.util.nbt;

import java.io.*;

public class NBTUtils {

    public static void write(NBT nbt, OutputStream outputStream) throws IOException {
        DataOutputStream output = new DataOutputStream(outputStream);

        int meta = NBT.VERSION << 24;
        output.writeInt(meta);

        output.writeByte(nbt.type());
        nbt.write(output);
    }

    public static <T extends NBT> T read(InputStream inputStream) throws IOException {
        DataInputStream input = new DataInputStream(inputStream);

        int meta = input.readInt();

        byte type = input.readByte();
        NBT nbt = NBT.createNBT(type);
        nbt.read(input);

        return (T)nbt;
    }

}
