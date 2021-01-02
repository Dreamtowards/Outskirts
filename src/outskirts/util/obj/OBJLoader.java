package outskirts.util.obj;

import outskirts.client.material.Model;
import outskirts.client.material.ex.ModelData;
import outskirts.client.render.VertexBuffer;
import outskirts.util.CollectionUtils;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static outskirts.util.StringUtils.explode;
import static outskirts.util.StringUtils.explodeSpaces;

public class OBJLoader {

    public static ModelData loadOBJ(InputStream inputStream) {
        List<Vector3f> tbPositions = new ArrayList<>();     // OBJ data table
        List<Vector2f> tbTextureCoords = new ArrayList<>(); // OBJ data table
        List<Vector3f> tbNormals = new ArrayList<>();       // OBJ data table
        List<String> rawVertices = new ArrayList<>();       // all vertices. allowed duplicate elements.

        readObjFile(inputStream, tbPositions, tbTextureCoords, tbNormals, rawVertices);

        List<String> tbDisjointVertices = new ArrayList<>(new HashSet<>(rawVertices));

        // exporting data.
        int tbvtxsz = tbDisjointVertices.size(); // "vertex table" size.
        int[] out_indices = new int[rawVertices.size()]; {
            for (int i = 0;i < rawVertices.size();i++) {
                out_indices[i] = tbDisjointVertices.indexOf(rawVertices.get(i));
            }
        }
        float[] out_positions = new float[tbvtxsz * 3];
        float[] out_textureCoords = new float[tbvtxsz * 2];
        float[] out_normals = new float[tbvtxsz * 3];

        int i = 0;
        for (String objv : tbDisjointVertices) {
            String[] bound = objv.split("/");

            Vector3f POS = tbPositions.get(Integer.parseInt(bound[0])-1);
            out_positions[i*3]   = POS.x;
            out_positions[i*3+1] = POS.y;
            out_positions[i*3+2] = POS.z;

            Vector2f TEX = tbTextureCoords.get(Integer.parseInt(bound[1])-1);
            out_textureCoords[i * 2] = TEX.x;
            out_textureCoords[i * 2 + 1] = TEX.y;

            Vector3f NORM = tbNormals.get(Integer.parseInt(bound[2])-1);
            out_normals[i * 3] = NORM.x;
            out_normals[i * 3 + 1] = NORM.y;
            out_normals[i * 3 + 2] = NORM.z;
            i++;
        }

        return new ModelData(out_indices, out_positions, out_textureCoords, out_normals);
    }

    private static void readObjFile(InputStream inputStream, List<Vector3f> positions, List<Vector2f> textureCoords, List<Vector3f> normals, List<String> rawVertices) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        int lineNum = 0;
        boolean tableCompleted = false;
        try {
        while ((line=br.readLine()) != null) {
            if (line.startsWith("v ")) { assert !tableCompleted;
                String[] s = explodeSpaces(line);
                positions.add(new Vector3f(parseFloat(s[1]), parseFloat(s[2]), parseFloat(s[3])));
            } else if (line.startsWith("vt ")) { assert !tableCompleted;
                String[] s = explodeSpaces(line);
                textureCoords.add(new Vector2f(parseFloat(s[1]), parseFloat(s[2])));
            } else if (line.startsWith("vn ")) { assert !tableCompleted;
                String[] s = explodeSpaces(line);
                normals.add(new Vector3f(parseFloat(s[1]), parseFloat(s[2]), parseFloat(s[3])));
            } else if (line.startsWith("f ")) {  // v,vt,vn table all beeing Read-Only when start reading f.  but not a exception validate
                tableCompleted=true;
                String[] s = explodeSpaces(line);
                rawVertices.add(s[1]);
                rawVertices.add(s[2]);
                rawVertices.add(s[3]);
            }
            lineNum++;
        }
        } catch (Exception ex) {
            throw new RuntimeException("Wrong while reading OBJ file. lineNum/"+lineNum+".", ex);
        }
    }




    private static boolean OP_FASTSAVE = false;

    // fast vers. but big output size.
    public static String saveOBJ(Model model) {
        return OBJLoader.saveOBJ(model.indices, model.attribute(0).data, model.attribute(1).data, model.attribute(2).data);
    }
    public static String saveOBJ(VertexBuffer vbuf) {
        return OBJLoader.saveOBJ(CollectionUtils.range(vbuf.positions.size()/3),
                CollectionUtils.toArrayf(vbuf.positions),
                CollectionUtils.toArrayf(vbuf.textureCoords),
                CollectionUtils.toArrayf(vbuf.normals));
    }
    public static String saveOBJ(int[] vindex, float[] positions, float[] textureCoords, float[] normals) {
        StringBuilder sb = new StringBuilder();
        if (OP_FASTSAVE) {
            for (int i = 0;i < positions.length;i+=3) {
                sb.append("v ").append(positions[i]).append(" ").append(positions[i+1]).append(" ").append(positions[i+2]).append("\n");
            }
            for (int i = 0;i < textureCoords.length;i+=2) {
                sb.append("vt ").append(textureCoords[i]).append(" ").append(textureCoords[i+1]).append("\n");
            }
            for (int i = 0;i < normals.length;i+=3) {
                sb.append("vn ").append(normals[i]).append(" ").append(normals[i+1]).append(" ").append(normals[i+2]).append("\n");
            }

            for (int i = 0;i < vindex.length;i+=3) {
                sb.append("f ").append(vindex[i]+1).append("/").append(vindex[i]+1).append("/").append(vindex[i]+1)
                  .append(" ").append(vindex[i+1]+1).append("/").append(vindex[i+1]+1).append("/").append(vindex[i+1]+1)
                  .append(" ").append(vindex[i+2]+1).append("/").append(vindex[i+2]+1).append("/").append(vindex[i+2]+1).append("\n");
            }
            return sb.toString();
        }
        // build table.
        List<Vector3f> positionsTable = new ArrayList<>();
        List<Vector2f> textureCoordsTable = new ArrayList<>();
        List<Vector3f> normalsTable = new ArrayList<>();
        for (int i = 0;i < positions.length;i+=3) {
            Vector3f v = new Vector3f(positions[i], positions[i+1], positions[i+2]);
            if (!positionsTable.contains(v))
                positionsTable.add(v);
        }
        for (int i = 0;i < textureCoords.length;i+=2) {
            Vector2f v = new Vector2f(textureCoords[i], textureCoords[i+1]);
            if (!textureCoordsTable.contains(v))
                textureCoordsTable.add(v);
        }
        for (int i = 0;i < normals.length;i+=3) {
            Vector3f v = new Vector3f(normals[i], normals[i+1], normals[i+2]);
            if (!normalsTable.contains(v))
                normalsTable.add(v);
        }
        // write sources.
        for (Vector3f v : positionsTable) {
            sb.append("v ").append(v.x).append(" ").append(v.y).append(" ").append(v.z).append("\n");
        }
        for (Vector2f vt : textureCoordsTable) {
            sb.append("vt ").append(vt.x).append(" ").append(vt.y).append("\n");
        }
        for (Vector3f vn : normalsTable) {
            sb.append("vn ").append(vn.x).append(" ").append(vn.y).append(" ").append(vn.z).append("\n");
        }
        // indices faces
        Vector3f TMPv = new Vector3f();
        Vector2f TMPvt = new Vector2f();
        Vector3f TMPvn = new Vector3f();
        for (int i = 0;i < vindex.length;i+=3) {
            TMPv.set(positions[vindex[i]*3], positions[vindex[i]*3+1], positions[vindex[i]*3+2]);
            TMPvt.set(textureCoords[vindex[i]*2], textureCoords[vindex[i]*2+1]);
            TMPvn.set(normals[vindex[i]*3], normals[vindex[i]*3+1], normals[vindex[i]*3+2]);
            sb.append("f ").append(positionsTable.indexOf(TMPv)+1).append("/").append(textureCoordsTable.indexOf(TMPvt)+1).append("/").append(normalsTable.indexOf(TMPvn)+1);

            TMPv.set(positions[vindex[i+1]*3], positions[vindex[i+1]*3+1], positions[vindex[i+1]*3+2]);
            TMPvt.set(textureCoords[vindex[i+1]*2], textureCoords[vindex[i+1]*2+1]);
            TMPvn.set(normals[vindex[i+1]*3], normals[vindex[i+1]*3+1], normals[vindex[i+1]*3+2]);
            sb.append(" ").append(positionsTable.indexOf(TMPv)+1).append("/").append(textureCoordsTable.indexOf(TMPvt)+1).append("/").append(normalsTable.indexOf(TMPvn)+1);

            TMPv.set(positions[vindex[i+2]*3], positions[vindex[i+2]*3+1], positions[vindex[i+2]*3+2]);
            TMPvt.set(textureCoords[vindex[i+2]*2], textureCoords[vindex[i+2]*2+1]);
            TMPvn.set(normals[vindex[i+2]*3], normals[vindex[i+2]*3+1], normals[vindex[i+2]*3+2]);
            sb.append(" ").append(positionsTable.indexOf(TMPv)+1).append("/").append(textureCoordsTable.indexOf(TMPvt)+1).append("/").append(normalsTable.indexOf(TMPvn)+1);

            sb.append("\n");
        }
        return sb.toString();
    }
}
