package outskirts.client.render;

import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.StringUtils;
import outskirts.util.mx.VertexUtil;
import outskirts.util.obj.OBJLoader;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// todo: mode indexing.?
public final class VertexBuffer {

    public List<Float> positions = new ArrayList<>();
    public List<Float> textureCoords = new ArrayList<>();
    public List<Float> normals = new ArrayList<>();

    public void addpos(float x, float y, float z) {
        positions.add(x);
        positions.add(y);
        positions.add(z);
    }
    public void addpos(Vector3f v) {
        addpos(v.x, v.y, v.z);
    }
    public void getpos(int i, Vector3f dest) {
        dest.x = positions.get(i);
        dest.y = positions.get(i+1);
        dest.z = positions.get(i+2);
    }

    public void adduv(float x, float y) {
        textureCoords.add(x);
        textureCoords.add(y);
    }
    public void adduv(Vector2f v) {
        adduv(v.x, v.y);
    }

    public void addnorm(float x, float y, float z) {
        normals.add(x);
        normals.add(y);
        normals.add(z);
    }
    public void addnorm(Vector3f v) {
        addnorm(v.x, v.y, v.z);
    }
    public void setnorm(int i, float x, float y, float z) {
        normals.set(i,   x);
        normals.set(i+1, y);
        normals.set(i+2, z);
    }
    public void setnorm(int i, Vector3f n) {
        setnorm(i, n.x, n.y, n.z);
    }


    public float[] posarr() {
        return CollectionUtils.toArrayf(positions);
    }
    public float[] uvarr() {
        return CollectionUtils.toArrayf(textureCoords);
    }
    public float[] normarr() {
        return CollectionUtils.toArrayf(normals);
    }


    public void inituvnorm() {
        for (int i = 0;i < positions.size()/3;i++) {
            adduv(0, 0);
            addnorm(0, 0, 0);
        }
        VertexUtil.hardnorm(this);
    }
    public void tmpsaveobjfile(String filename) {
        try {
            IOUtils.write(OBJLoader.saveOBJ(this), new FileOutputStream(filename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
