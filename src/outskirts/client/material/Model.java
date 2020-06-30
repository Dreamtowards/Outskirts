package outskirts.client.material;

import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

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

    private int eboID;

    private int[] vboID = new int[16];

    private int vertexCount;

    private Model() { }

    public static Model glwGenVertexArrays() {  // needs fill Indices data when creating.?
        Model model = new Model();
        model.vaoID = glGenVertexArrays();

        return model;
    }

    public int vaoID() {
        return vaoID;
    }

    public int eboID() {
        return eboID;
    }

    public int vboID(int i) {
        return vboID[i];
    }

    public int vertexCount() {
        return vertexCount;
    }

    public void bindVertexArray() {
        glBindVertexArray(vaoID);
    }

    public void createElementBuffer(int[] indices) {
        this.vertexCount = indices.length;
        this.eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void createAttribute(int attributeNumber, int vertexSize, float[] data) {
        int vbo = glGenBuffers();
        this.vboID[attributeNumber] = vbo;
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        glVertexAttribPointer(attributeNumber, vertexSize, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attributeNumber);
    }

    // glBufferSubData

    // actually only assoc with VBO, not Attribute..
    public void setAttributeSubData(int attributeNumber, float[] data) {
        glBindBuffer(GL_ARRAY_BUFFER, vboID[attributeNumber]);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
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
