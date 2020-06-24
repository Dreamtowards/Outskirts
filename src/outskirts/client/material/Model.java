package outskirts.client.material;

import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;

/**
 * A VAO Object.
 *
 * if uses a public instance field, though "convenience", "faster" (both execution speed and write/call speed)
 * but its given people a Casual feeling.. like a tmp design, and feels unsafe cuz its some way is a public uncontrollable.
 *
 * Method given people more Trust && StrongPower than public Field in subconscious
 * those getter not "get" prefix cuz the prefix have some turn down the mainly feel.
 */
public final class Model {

    private int vaoID;

    /** If not EBO, (use GL_ARRAY_BUFFER) that eboID will be 0 */
    private int eboID;

    private int[] vboID;

    private int vertexCount;

    public Model(int vaoID, int eboID, int[] vboID, int vertexCount) {
        this.vaoID = vaoID;
        this.eboID = eboID;
        this.vboID = vboID;
        this.vertexCount = vertexCount;
    }

    public int vaoID() {
        return vaoID;
    }

    public int eboID() {
        return eboID;
    }

    public int[] vboID() {
        return vboID;
    }

    public int vertexCount() {
        return vertexCount;
    }

    public void delete() {

        if (eboID != -1) {
            glDeleteBuffers(eboID);
        }

        for (int vbo : vboID) {
            glDeleteBuffers(vbo);
        }

        glDeleteVertexArrays(vaoID);
    }
}
