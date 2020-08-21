package outskirts.storage.tools;

import org.json.JSONObject;
import outskirts.storage.dat.DST;
import outskirts.storage.dat.DSTUtils;
import outskirts.util.IOUtils;
import outskirts.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DstJsonConvert {

    public static boolean BYTEARRAY_BASE64STR = true;

    public static void main(String[] args) throws IOException {
        String mode = args[0];
        String fpath = args[1];

        File srcFile = new File(fpath);
        if (mode.equals("tojson")) {
            Map mp = DSTUtils.read(new FileInputStream(srcFile));
            replace(mp, DST.BYTE_ARRAY, barray -> Base64.getEncoder().encodeToString((byte[])barray) );
            String out = new JSONObject(mp).toString(2);
            File destFile = new File(srcFile.getPath() + ".json");
            IOUtils.write(new ByteArrayInputStream(out.getBytes(StandardCharsets.UTF_8)), new FileOutputStream(destFile));
            System.out.println(String.format("ParseDAT: %s, -> outJSON: %s", srcFile.getPath(), destFile.getPath()));
        } else if (mode.equals("fromjson")) {
            Map mp = new JSONObject(IOUtils.toString(new FileInputStream(srcFile), StandardCharsets.UTF_8)).toMap();
            File destFile = new File(srcFile.getPath() + ".bin");
            DSTUtils.write(mp, new FileOutputStream(destFile));
            System.out.println(String.format("ParseJSON: %s, -> outDAT: %s", srcFile.getPath(), destFile.getPath()));
        }
    }

    private static void replace(Object dat, byte replaceType, Function replacemet) {
        int type = DST.type(dat);
        if (type == DST.LIST) {
            List l = (List)dat;
            byte lType = DST.type(l.get(0));
            for (int i = 0;i < l.size();i++) {
                if (DST.isStruct(lType)) {
                    replace(l.get(i), replaceType, replacemet);
                } else if (lType == replaceType) {
                    l.set(i, replacemet.apply(l.get(i)));
                }
            }
        } else if (type == DST.MAP) {
            Map<String, Object> m = (Map)dat;
            for (String k : m.keySet()) {
                Object v = m.get(k);
                byte vType = DST.type(v);
                if (DST.isStruct(vType)) {
                    replace(v, replaceType, replacemet);
                } else if (vType == replaceType) {
                    m.put(k, replacemet.apply(v));
                }
            }
        }
    }
}
