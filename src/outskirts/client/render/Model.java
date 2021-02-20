package outskirts.client.render;

import outskirts.client.Loader;
import outskirts.util.IOUtils;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
//todo move Model, Texture to client.render package. (.?)
    //todo: should Model been data-free-changeable type.? like vec.s. ? 
public final class Model {

    private int vaoID;
    private int eboID;
    public int[] indices;  // readonly.  // indices.length == vertexCount.
    private VAttribute[] vbos = new VAttribute[16]; // these VBOs recorspounding each AttributeList elements.

    private Model() { }

    public static Model glfGenVertexArrays() {  // needs fill Indices data when creating.?
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

    public VAttribute attribute(int i) {
        return vbos[i];
    }

    // dep.?
    public int vertexCount() {
        return indices.length;
    }

    public void createEBO(int[] indices) {
        glBindVertexArray(vaoID);

        this.indices = indices;
        this.eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Loader.loadBuffer(indices), GL_STATIC_DRAW);
    }

    Queue<ByteBuffer> bf = new LinkedList<>();
    public void disposeBufs() {
        while (!bf.isEmpty()) {

            // todo: edit.
        }
    }
    public static void dsps(ByteBuffer buffer) throws Exception {
        Method cleanerMethod = buffer.getClass().getMethod("cleaner");
        cleanerMethod.setAccessible(true);
        Object cleaner = cleanerMethod.invoke(buffer);
        Method cleanMethod = cleaner.getClass().getMethod("clean");
        cleanMethod.setAccessible(true);
        cleanMethod.invoke(cleaner);
    }

    public void createAttribute(int attributeNumber, int vertexSize, float[] data) {
        glBindVertexArray(vaoID);

        VAttribute vbo = this.vbos[attributeNumber] = VAttribute.glfGenBuffers();
        vbo.vertexSize = vertexSize;
        vbo.setBufferData(data);

        glVertexAttribPointer(attributeNumber, vertexSize, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attributeNumber);
    }


    /**
     * Handling a data VBO, and 'also' AttributeList-Attribute info.
     *
     * create from Model.createAttribute().
     */
    public static final class VAttribute {

        private int vboID;
        private int vertexSize;  // attrib info.
        public float[] data;  // readonly.  // should FloatBuffer.?

        private VAttribute() {}

        private static VAttribute glfGenBuffers() {
            VAttribute vbo = new VAttribute();
            vbo.vboID = glGenBuffers();
            return vbo;
        }

        public int vertexSize() {
            return vertexSize;
        }

        public void setBufferData(float[] data) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);

            this.data = data;

            glBufferData(GL_ARRAY_BUFFER, Loader.loadBuffer(data), GL_STATIC_DRAW);
        }

        public void setBufferSubData(float[] subdata) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);

            System.arraycopy(subdata, 0, this.data, 0, subdata.length);

            glBufferSubData(GL_ARRAY_BUFFER, 0, Loader.loadBuffer(data));
        }


        public void delete() {

            glDeleteBuffers(vboID);
        }
    }


    public void delete() {

        if (eboID != -1) {
            glDeleteBuffers(eboID);
        }

        for (VAttribute vbo : vbos) {
            vbo.delete();
        }

        glDeleteVertexArrays(vaoID);
    }
}
