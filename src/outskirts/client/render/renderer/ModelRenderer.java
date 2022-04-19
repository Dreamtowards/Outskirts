package outskirts.client.render.renderer;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.client.render.Texture;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.*;
import outskirts.util.vector.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * ModelRenderer. 3D model renderer. more commonly, not so high-level as EntityRenderer.
 */
public class ModelRenderer extends Renderer {

    public static boolean useFog = false;

    private ShaderProgram shader = new ShaderProgram(
            new ResourceLocation("shaders/model.vsh").getInputStream(),
            new ResourceLocation("shaders/model.fsh").getInputStream()
    );

    private Matrix4f MAT_MODELMAT_TRANS = new Matrix4f();

    public void render(Model model, Texture texture, Vector3f position, Vector3f scale, Matrix3f rotation, Vector4f colorMultiply, boolean viewMatrix, boolean projectionMatrix, int mode) {

        shader.useProgram();

        shader.setVector4f("colorMultiply", colorMultiply);

        shader.setMatrix4f("viewMatrix", viewMatrix ? Outskirts.renderEngine.getViewMatrix() : Matrix4f.IDENTITY);
        shader.setMatrix4f("projectionMatrix", projectionMatrix ? Outskirts.renderEngine.getProjectionMatrix() : Matrix4f.IDENTITY);

        shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(position, scale, rotation, MAT_MODELMAT_TRANS));


        glBindVertexArray(model.vaoID());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.textureID());

        glDrawElements(mode, model.vertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    public final void drawOutline(AABB aabb, Vector4f color) {
        Vector3f aabbSize = Vector3f.sub(aabb.max, aabb.min, null);
        drawOutline(aabb.min, aabbSize, color);
    }

    private void drawOutline(Vector3f position, Vector3f size, Vector4f color) {
        render(MODEL_LINES_BOUNDINGBOX, Texture.UNIT,
                position,
                size, // the MODEL_CUBE_EDGE is oriented by origin, so scale==size
                Matrix3f.IDENTITY,
                color, true, true, GL_LINES);
    }

    private Vector3f UP_DIFF = new Vector3f(0, 1, 0.01f).normalize();
    public void drawLine(Vector3f pos1, Vector3f pos2, Vector4f color) {
        if (pos1.equals(pos2))
            return;
        Vector3f diff = Vector3f.sub(pos2, pos1, null);
        render(MODEL_LINE, Texture.UNIT,
                pos1,
                Vector3f.UNIT_Z,
                Maths.lookAt(diff, UP_DIFF, null),
                color, true, true, GL_LINES);
    }

    public void drawTriangleFace(Vector3f v0, Vector3f v1, Vector3f v2, Vector4f color) {
        MODEL_TRIANGLE.attribute(0).setBufferSubData(new float[]{v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z}); // test
        render(MODEL_TRIANGLE, Texture.UNIT, Vector3f.ZERO, Vector3f.ONE, Matrix3f.IDENTITY, color, true, true, GL_TRIANGLES);
    }



    public static Model M_HORIZ_PLANE = Loader.loadModel(3, new float[] {
            -1, 0,-1,-1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0,-1,-1, 0,-1
    }, 2, new float[] {
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0
    });
    /**
     *  0,5     4
     *  +-------+
     *  |  \    |
     *  |    \  |
     *  +-------+
     *  1       2,3
     */
    public static Model MODEL_CUBE = Loader.loadModel(3,new float[] { // positions. [-1.0, 1.0], 0.0 center.
             1, 1, 1, 1,-1, 1, 1,-1,-1, 1,-1,-1, 1, 1,-1, 1, 1, 1, // +X. Y_UP(face) pPass
            -1, 1,-1,-1,-1,-1,-1,-1, 1,-1,-1, 1,-1, 1, 1,-1, 1,-1, // -X. Y_UP
            -1, 1,-1,-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,-1,-1, 1,-1, // +Y. -Z_UP
            -1,-1, 1,-1,-1,-1, 1,-1,-1, 1,-1,-1, 1,-1, 1,-1,-1, 1, // -Y. Z_UP  //should -Z_UP ..?
            -1, 1, 1,-1,-1, 1, 1,-1, 1, 1,-1, 1, 1, 1, 1,-1, 1, 1, // +Z. Y_UP
             1, 1,-1, 1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 1,-1, 1, 1,-1  // -Z. Y_UP
    }, 2,new float[] { // textureCoords
            0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, // one Face
            0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1,
            0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1,
            0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1,
            0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1,
            0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1,
    }, 3,new float[] { // normals
             1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,  // +X
            -1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,  // -X
             0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,  // +Y
             0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0,  // -Y
             0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,  // +Z
             0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1,  // -Z
    }, 3,new float[] { // tangents  // +Z
             0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1,  // +X.  (-Z
             0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,  // -X   (+Z
             1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,  // +Y   (+X
             1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,  // -Y   (+X
             1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,  // +Z   (+X
            -1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,-1, 0, 0,  // -Z   (-X
    });//Models.GEO_CUBE;//Loader.loadModelWithTangent(null, DATA_CUBE_POSITIONS,3, DATA_CUBE_TEXTURECOORDS,2, DATA_CUBE_NORMALS,3);


    public static final Model MODEL_LINE = Loader.loadModel(3,new float[] {
            0, 0, 0,
            0, 0,-1
    });

    public static final Model MODEL_TRIANGLE = Loader.loadModel(3,new float[] {
            0, 0, 0,
            0, 0, 0,
            0, 0, 0
    });

    // based on the Origin
    private static final Model MODEL_LINES_BOUNDINGBOX = Loader.loadModel(3,new float[] {
            0, 0, 0, 0, 1, 0,   0, 0, 1, 0, 1, 1,   1, 0, 1, 1, 1, 1,   1, 0, 0, 1, 1, 0,
            0, 0, 0, 0, 0, 1,   0, 0, 1, 1, 0, 1,   1, 0, 1, 1, 0, 0,   1, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 1, 1,   0, 1, 1, 1, 1, 1,   1, 1, 1, 1, 1, 0,   1, 1, 0, 0, 1, 0,
    });

    // based on the Origin
    public static Model MODEL_CUBE_BZERO = Loader.loadModel(3,new float[] {
            1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1,
            0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0,
            0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0,
            0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1,
            0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
            1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0,
    });
}
