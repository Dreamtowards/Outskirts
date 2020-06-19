package outskirts.util.obj;

import outskirts.client.material.ModelData;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OBJFileLoader {

    public static ModelData loadOBJ(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        List<Vertex> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int linenum = 0;
        try {
            while (true) {
                line = br.readLine();
                linenum++;
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" +");
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    Vertex newVertex = new Vertex(vertices.size(), vertex);
                    vertices.add(newVertex);
                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" +");
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" +");
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }
            while (line != null && line.startsWith("f ")) {
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                processVertex(vertex1, vertices, indices);
                processVertex(vertex2, vertices, indices);
                processVertex(vertex3, vertices, indices);
                line = br.readLine();
                linenum++;
            }
            br.close();
        } catch (Exception ex) {
            throw new RuntimeException("Wrong to reading the obj file. line/"+linenum, ex);
        }
        removeUnusedVertices(vertices);
        float[] positionsArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float furthestPoint = convertDataToArrays(vertices, textures, normals, positionsArray, texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);
        return new ModelData(indicesArray, positionsArray, texturesArray, normalsArray);
    }

    private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]);
        int textureIndex = Integer.parseInt(vertex[1]);
        int normalIndex = Integer.parseInt(vertex[2]);
        index += index>=0? -1 : vertices.size();
        textureIndex += textureIndex>=0? -1 : vertices.size();
        normalIndex += normalIndex>=0? -1 : vertices.size();
        Vertex currentVertex = vertices.get(index);
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
        } else {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
        }
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoords = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoords.x;
            texturesArray[i * 2 + 1] = 1 - textureCoords.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
        return furthestPoint;
    }

    private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
                        indices, vertices);
            } else {
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
            }

        }
    }

    private static void removeUnusedVertices(List<Vertex> vertices){
        for(Vertex vertex:vertices){
            if(!vertex.isSet()){
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

    private static class Vertex {

        private static final int NO_INDEX = -1;

        private Vector3f position;
        private int textureIndex = NO_INDEX;
        private int normalIndex = NO_INDEX;
        private Vertex duplicateVertex = null;
        private int index;
        private float length;

        public Vertex(int index, Vector3f position){
            this.index = index;
            this.position = position;
            this.length = position.length();
        }

        public int getIndex(){
            return index;
        }

        public float getLength(){
            return length;
        }

        public boolean isSet(){
            return textureIndex!=NO_INDEX && normalIndex!=NO_INDEX;
        }

        public boolean hasSameTextureAndNormal(int textureIndexOther,int normalIndexOther){
            return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
        }

        public void setTextureIndex(int textureIndex){
            this.textureIndex = textureIndex;
        }

        public void setNormalIndex(int normalIndex){
            this.normalIndex = normalIndex;
        }

        public Vector3f getPosition() {
            return position;
        }

        public int getTextureIndex() {
            return textureIndex;
        }

        public int getNormalIndex() {
            return normalIndex;
        }

        public Vertex getDuplicateVertex() {
            return duplicateVertex;
        }

        public void setDuplicateVertex(Vertex duplicateVertex) {
            this.duplicateVertex = duplicateVertex;
        }


    }
}
