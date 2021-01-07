package outskirts.client.render;

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
}
