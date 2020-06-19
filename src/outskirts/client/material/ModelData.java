package outskirts.client.material;

public final class ModelData {

    public int[] indices;
    public float[] positions;
    public float[] textureCoords;
    public float[] normals;

    public ModelData(int[] indices, float[] positions, float[] textureCoords, float[] normals) {
        this.indices = indices;
        this.positions = positions;
        this.textureCoords = textureCoords;
        this.normals = normals;
    }

    //TODO: really work?
    public void delete() {
        indices = null;
        positions = null;
        textureCoords = null;
        normals = null;
    }
}
