package outskirts.util.obj;

import outskirts.client.material.Model;
import outskirts.client.material.ModelData;
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
        List<Vector3f> positions = new ArrayList<>();     // OBJ data table
        List<Vector2f> textureCoords = new ArrayList<>(); // OBJ data table
        List<Vector3f> normals = new ArrayList<>();       // OBJ data table
        List<Vertex> vertices = new ArrayList<>();        // Unique Vertex ProcessedTable
        List<Integer> indices = new ArrayList<>();        // Indiceing Vertex to "getting a model".

        readObjFile(inputStream, positions, textureCoords, normals, vertices, indices);

        // exporting data.
        float[] positionsArray = new float[vertices.size() * 3];
        float[] textureCoordsArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        int[] indicesArray = new int[indices.size()];

        for (int i = 0;i < indices.size();i++) {
            int vIndex = indices.get(i); // index to VertexData
            indicesArray[i] = vIndex;
            Vertex v = vertices.get(vIndex);

            Vector3f POS = positions.get(v.positionIndex);
            positionsArray[vIndex*3]   = POS.x;
            positionsArray[vIndex*3+1] = POS.y;
            positionsArray[vIndex*3+2] = POS.z;

            Vector2f TEX = textureCoords.get(v.textureIndex);
            textureCoordsArray[vIndex*2]   = TEX.x;
            textureCoordsArray[vIndex*2+1] = TEX.y;

            Vector3f NORM = normals.get(v.normalIndex);
            normalsArray[vIndex*3]   = NORM.x;
            normalsArray[vIndex*3+1] = NORM.y;
            normalsArray[vIndex*3+2] = NORM.z;
        }

        return new ModelData(indicesArray, positionsArray, textureCoordsArray, normalsArray);
    }

    private static void readObjFile(InputStream inputStream, List<Vector3f> positions, List<Vector2f> textureCoords, List<Vector3f> normals, List<Vertex> vertices, List<Integer> indices) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        int lineNum = 0;
        try {
            while ((line=br.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] s = explodeSpaces(line);
                    positions.add(new Vector3f(parseFloat(s[1]), parseFloat(s[2]), parseFloat(s[3])));
                } else if (line.startsWith("vt ")) {
                    String[] s = explodeSpaces(line);
                    textureCoords.add(new Vector2f(parseFloat(s[1]), parseFloat(s[2])));
                } else if (line.startsWith("vn ")) {
                    String[] s = explodeSpaces(line);
                    normals.add(new Vector3f(parseFloat(s[1]), parseFloat(s[2]), parseFloat(s[3])));
                } else if (line.startsWith("f ")) {
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
        int positionIndex = parseInt(face_vi_ptn[0]) - 1;
        int textureIndex = parseInt(face_vi_ptn[1]) - 1;
        int normalIndex = parseInt(face_vi_ptn[2]) - 1;
        int i = 0;
        for (Vertex v : vertices) {
            if (v.positionIndex==positionIndex && v.textureIndex==textureIndex && v.normalIndex==normalIndex) { // exists the vertex
                indices.add(i);
                return;
            }
            i++;
        }
        // non exists the vertex. create a new.
        vertices.add(new Vertex(positionIndex, textureIndex, normalIndex));
        indices.add(vertices.size()-1);
    }

    private static class Vertex {
        int positionIndex;
        int textureIndex;
        int normalIndex;

        Vertex(int positionIndex, int textureIndex, int normalIndex) {
            this.positionIndex = positionIndex;
            this.textureIndex = textureIndex;
            this.normalIndex = normalIndex;
        }
    }
}
