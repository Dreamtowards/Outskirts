package outskirts.client.render.shader;

import org.lwjgl.BufferUtils;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;
import outskirts.util.vector.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static outskirts.util.logging.Log.LOGGER;

/**
 * ShaderProgram controls/interactives a GLSL-shader-program/shaders.
 * its a shader-program not just shaders. its a shader-programs composited by GLSL-shaders.
 */
public final class ShaderProgram {

    private int programID;

    private int vertexShader;
    private int fragmentShader;

    private Map<String, Integer> uniformLocationMap = new HashMap<>(); // cache of UniformLocation(s). for lesser VM access

    public ShaderProgram(InputStream vertexShaderSource, InputStream fragmentShaderSource) {
        try {
            vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderSource);
            fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load shader.", ex);
        }

        programID = glCreateProgram();
        glAttachShader(programID, vertexShader);
        glAttachShader(programID, fragmentShader);

        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            LOGGER.warn("##### GL Program Link Error #####");
            LOGGER.warn(glGetProgramInfoLog(programID, 1024));
            throw new IllegalStateException("GL Program Link Error.");
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private static int loadShader(int shaderType, InputStream sourceInputStream) throws IOException {
        int shaderID = glCreateShader(shaderType);

        String shaderSource = IOUtils.toString(sourceInputStream);

        glShaderSource(shaderID, shaderSource);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            String shadername = getShaderTypeName(shaderType);
            LOGGER.warn("##### GL Shader Compile Error ({}) #####", shadername);
            LOGGER.warn(glGetShaderInfoLog(shaderID, 1024));
            throw new IllegalStateException("GL Shader Compile Error (" + shadername + ")");
        }

        return shaderID;
    }

    private static String getShaderTypeName(int shaderType) {
        switch (shaderType) {
            case GL_VERTEX_SHADER:
                return "GL_VERTEX_SHADER";
            case GL_FRAGMENT_SHADER:
                return "GL_FRAGMENT_SHADER";
            case GL_GEOMETRY_SHADER:
                return "GL_GEOMETRY_SHADER";
        }
        return "_UNKNOWN_TYPE";
    }

    public void useProgram() {
        glUseProgram(programID);
    }

    private int getUniformLocation(String uniformName) {
        return uniformLocationMap.computeIfAbsent(uniformName, nm -> glGetUniformLocation(programID, uniformName));
    }

    private void delete() {

        glDetachShader(programID, vertexShader);
        glDetachShader(programID, fragmentShader);

        glDeleteProgram(programID);
    }

    public void setInt(String uniformName, int value) {
        glUniform1i(getUniformLocation(uniformName), value);
    }

    public void setFloat(String uniformName, float value) {
        glUniform1f(getUniformLocation(uniformName), value);
    }

    public void setMatrix2f(String uniformName, Matrix2f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
        Matrix2f.store(matrix, buffer);
        buffer.flip();
        glUniformMatrix2fv(getUniformLocation(uniformName), true, buffer);
    }

    public void setMatrix3f(String uniformName, Matrix3f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        Matrix3f.store(matrix, buffer);
        buffer.flip();
        glUniformMatrix3fv(getUniformLocation(uniformName), true, buffer);
    }

    public void setMatrix4f(String uniformName, Matrix4f matrix) {
        glUniformMatrix4fv(getUniformLocation(uniformName), true, Matrix4f.store(matrix, new float[16]));
    }

    public void setVector2f(String uniformName, Vector2f vector) {
        setVector2f(uniformName, vector.x, vector.y);
    }
    public void setVector2f(String uniformName, float x, float y) {
        glUniform2f(getUniformLocation(uniformName), x, y);
    }

    public void setVector3f(String uniformName, Vector3f vector) {
        glUniform3f(getUniformLocation(uniformName), vector.x, vector.y, vector.z);
    }

    public void setVector4f(String uniformName, Vector4f vector) {
        glUniform4f(getUniformLocation(uniformName), vector.x, vector.y, vector.z, vector.w);
    }

}
