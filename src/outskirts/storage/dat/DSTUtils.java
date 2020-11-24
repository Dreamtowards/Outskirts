package outskirts.storage.dat;

import outskirts.util.FileUtils;
import outskirts.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class DSTUtils {


    public static DATObject read(InputStream is) throws IOException {
        // MAGNUM., VERNUM.
        byte type = IOUtils.readByte(is);
        assert type==DST.MAP;
        return (DATObject) DST.read(is, type);
    }

    public static void write(Map obj, OutputStream os) throws IOException {
        byte type = DST.type(obj);
        assert type==DST.MAP;
        IOUtils.writeByte(os, type);
        DST.write(os, type, obj);
    }
}
