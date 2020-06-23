package outskirts.util.obj;

import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Flip UV V.
 * texture coords: (U, 1-V)
 *
 * Some software /obj model resources, they uses different strienges, Auto flip tex Y coords...
 * like Blender exports.
 */
public class OBJFileUVFixer {

    public static String flipV(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line=br.readLine()) != null) {
            if (line.startsWith("vt")) {
                String[] bounds = line.split(" ");
                float flippedV = 1f - Float.parseFloat(bounds[2]);
                line = "vt "+bounds[1]+" "+flippedV;
            }
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        String filename = "src/assets/outskirts/materials/mount/Roline1.obj";//args[0];

        Log.LOGGER.info("Opening file...");
//        String filefixed = flipV(new FileInputStream(filename));
        String filefixed = OBJFileVVtVnFRegularizer.regu(new FileInputStream(filename));
        Log.LOGGER.info("Proccessed.");

        IOUtils.write(new ByteArrayInputStream(filefixed.getBytes(StandardCharsets.UTF_8)), new FileOutputStream(filename+"fx"));
        Log.LOGGER.info("Saved done.");
    }

}
