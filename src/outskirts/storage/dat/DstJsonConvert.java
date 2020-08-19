package outskirts.storage.dat;

import org.json.JSONObject;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DstJsonConvert {

    public static void main(String[] args) throws IOException {
        String mode = args[0];
        String fpath = args[1];

        File srcFile = new File(fpath);
        if (mode.equals("tojson")) {
            String out = new JSONObject(DSTUtils.read(new FileInputStream(srcFile))).toString(2);
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

}
