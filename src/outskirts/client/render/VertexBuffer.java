package outskirts.client.render;

import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class VertexBuffer {

    public List<Float> positions = new ArrayList<>();
    public List<Float> textureCoords = new ArrayList<>();
    public List<Float> normals = new ArrayList<>();

    public void addpos(float x, float y, float z) {
        positions.add(x);
        positions.add(y);
        positions.add(z);
    }

    public void adduv(float x, float y) {
        textureCoords.add(x);
        textureCoords.add(y);
    }

    public void addnorm(float x, float y, float z) {
        normals.add(x);
        normals.add(y);
        normals.add(z);
    }
    public void setnorm(int i, float x, float y, float z) {
        normals.set(i,   x);
        normals.set(i+1, y);
        normals.set(i+2, z);
    }
    public void setnorm(int i, Vector3f n) {
        setnorm(i, n.x, n.y, n.z);
    }
}
