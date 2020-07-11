package outskirts.util.obj;

import outskirts.util.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OBJFileVVtVnFRegularizer {

    public static void main(String[] args) throws Exception {

        String s = regu(new FileInputStream("src/assets/outskirts/materials/exa1.obj"));

        IOUtils.write(new ByteArrayInputStream(s.getBytes()), new FileOutputStream("src/assets/outskirts/materials/exa1.objfx"));

    }

    public static String regu(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        List<String> ls_v = new ArrayList<>();
        List<String> ls_vt = new ArrayList<>();
        List<String> ls_vn = new ArrayList<>();
        List<String> ls_f = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line=br.readLine()) != null) {
            if (line.startsWith("v ")) ls_v.add(line);
            else if (line.startsWith("vt ")) ls_vt.add(line);
            else if (line.startsWith("vn ")) ls_vn.add(line);
            else if (line.startsWith("f ")) ls_f.add(line);
            else sb.append(line).append("\n");
        }
        for (String l : ls_v) sb.append(l).append("\n");
        for (String l : ls_vt) sb.append(l).append("\n");
        for (String l : ls_vn) sb.append(l).append("\n");
        for (String l : ls_f) sb.append(l).append("\n");
        return sb.toString();
    }

}
