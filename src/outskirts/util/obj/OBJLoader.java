package outskirts.util.obj;

import outskirts.client.material.ModelData;
import outskirts.util.CollectionUtils;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static outskirts.util.StringUtils.explode;
import static outskirts.util.StringUtils.explodeSpaces;

public class OBJLoader {

    public static ModelData loadOBJ(InputStream inputStream) {
        List<Vector3f> tbPositions = new ArrayList<>();     // OBJ data table
        List<Vector2f> tbTextureCoords = new ArrayList<>(); // OBJ data table
        List<Vector3f> tbNormals = new ArrayList<>();       // OBJ data table

        List<Vertex> vertices = new ArrayList<>();        // Disjoint Vertices. 'init-array' aligned tbPositions. extends Vertices by other vert types data.
        List<Integer> indices = new ArrayList<>();        // Indiceing Vertex to "getting a model".

        readObjFile(inputStream, tbPositions, tbTextureCoords, tbNormals, vertices, indices);

        // exporting data.
        int[] out_indices = CollectionUtils.toArrayi(indices);
        float[] out_positions = new float[vertices.size() * 3];
        float[] out_textureCoords = new float[vertices.size() * 2];
        float[] out_normals = new float[vertices.size() * 3];

        for (int i = 0;i < vertices.size();i++) {
            Vertex v = vertices.get(i);

            Vector3f POS = tbPositions.get(v.positionIdx);
            out_positions[i*3]   = POS.x;
            out_positions[i*3+1] = POS.y;
            out_positions[i*3+2] = POS.z;

            Vector2f TEX = tbTextureCoords.get(v.textureIdx);
            out_textureCoords[i*2]   = TEX.x;
            out_textureCoords[i*2+1] = TEX.y;

            Vector3f NORM = tbNormals.get(v.normalIdx);
            out_normals[i*3]   = NORM.x;
            out_normals[i*3+1] = NORM.y;
            out_normals[i*3+2] = NORM.z;
        }

        return new ModelData(out_indices, out_positions, out_textureCoords, out_normals);
    }

    private static void readObjFile(InputStream inputStream, List<Vector3f> positions, List<Vector2f> textureCoords, List<Vector3f> normals, List<Vertex> vertices, List<Integer> indices) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        int lineNum = 0;
        boolean tableCompleted = false;
        try {
        while ((line=br.readLine()) != null) {
            if (line.startsWith("v ")) { assert !tableCompleted;
                String[] s = explodeSpaces(line);
                positions.add(new Vector3f(parseFloat(s[1]), parseFloat(s[2]), parseFloat(s[3])));
                vertices.add(new Vertex(positions.size()-1, -1, -1));
            } else if (line.startsWith("vt ")) { assert !tableCompleted;
                String[] s = explodeSpaces(line);
                textureCoords.add(new Vector2f(parseFloat(s[1]), parseFloat(s[2])));
            } else if (line.startsWith("vn ")) { assert !tableCompleted;
                String[] s = explodeSpaces(line);
                normals.add(new Vector3f(parseFloat(s[1]), parseFloat(s[2]), parseFloat(s[3])));
            } else if (line.startsWith("f ")) {  // v,vt,vn table all beeing Read-Only when start reading f.  but not a exception validate
                tableCompleted=true;
                String[] s = explodeSpaces(line);
                String[] v1i_ptn = explode(s[1], "/");
                String[] v2i_ptn = explode(s[2], "/");
                String[] v3i_ptn = explode(s[3], "/");
                processFaceVertex(v1i_ptn, vertices, indices);
                processFaceVertex(v2i_ptn, vertices, indices);
                processFaceVertex(v3i_ptn, vertices, indices);
            }
            lineNum++;
        }
        } catch (Exception ex) {
            throw new RuntimeException("Wrong while reading OBJ file. lineNum/"+lineNum+".", ex);
        }
    }
    private static void processFaceVertex(String[] face_vi_ptn, List<Vertex> vertices, List<Integer> indices) {
        int positionIdx = parseInt(face_vi_ptn[0]) - 1;
        int textureIdx = parseInt(face_vi_ptn[1]) - 1;
        int normalIdx = parseInt(face_vi_ptn[2]) - 1;

        Vertex bv = vertices.get(positionIdx);  // 'basic vert'. related extended Vertex.
        if (bv.textureIdx == -1) {  // || bv.normal.Idx == -1.
            bv.textureIdx = textureIdx;
            bv.normalIdx  = normalIdx;
            indices.add(positionIdx);
        } else {
            addVertex(bv, textureIdx, normalIdx, vertices, indices);
        }
    }

    // when creating new VertexSet, using 'Extend' method.
    private static void addVertex(Vertex bv, int texIdx, int normIdx, List<Vertex> vertices, List<Integer> indices) {
        if (bv.textureIdx==texIdx || bv.normalIdx==normIdx) {
            indices.add(bv.extenVertexIdx==-1?bv.positionIdx: bv.extenVertexIdx);
            return;
        }
        if (bv.extenVertex == null) {
            bv.extenVertex = new Vertex(bv.positionIdx, texIdx, normIdx);
            vertices.add(bv.extenVertex);
            bv.extenVertex.extenVertexIdx = vertices.size()-1;
            indices.add(bv.extenVertex.extenVertexIdx);
        } else {
            addVertex(bv.extenVertex, texIdx, normIdx, vertices, indices);
        }
    }

    private static class Vertex {
        int positionIdx;
        int textureIdx;
        int normalIdx;

        int extenVertexIdx=-1;
        Vertex extenVertex;

        Vertex(int positionIndex, int textureIndex, int normalIndex) {
            this.positionIdx = positionIndex;
            this.textureIdx = textureIndex;
            this.normalIdx = normalIndex;
        }
    }
}
