package outskirts.client.render.shader;

import org.lwjgl.BufferUtils;
import outskirts.client.Loader;
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
    private int geometryShader = -1;

    private Map<String, Integer> uniformLocationMap = new HashMap<>(); // cache of UniformLocation(s). for lesser VM access

    public ShaderProgram(InputStream vsh, InputStream fsh, InputStream gsh) {

        vertexShader = loadShader(GL_VERTEX_SHADER, vsh);
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, fsh);
        if (gsh != null)
            geometryShader = loadShader(GL_GEOMETRY_SHADER, gsh);

        programID = glCreateProgram();
        glAttachShader(programID, vertexShader);
        glAttachShader(programID, fragmentShader);
        if (geometryShader != -1)
            glAttachShader(programID, geometryShader);

        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            LOGGER.warn("##### GL Program Link Error #####");
            LOGGER.warn(glGetProgramInfoLog(programID, 1024));
            throw new IllegalStateException("GL Program Link Error.");
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        if (geometryShader != -1)
            glDeleteShader(geometryShader);
    }
    public ShaderProgram(InputStream vsh, InputStream fsh) {
        this(vsh, fsh, null);
    }

    private static int loadShader(int shaderType, InputStream srcis) {
        try {
            int shaderID = glCreateShader(shaderType);

            String shaderSrc = IOUtils.toString(srcis);

            glShaderSource(shaderID, shaderSrc);
            glCompileShader(shaderID);

            if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                String shadername = getShaderTypeName(shaderType);
                LOGGER.warn("##### GL Shader Compile Error ({}) #####", shadername);
                LOGGER.warn(glGetShaderInfoLog(shaderID, 1024));
                throw new IllegalStateException("GL Shader Compile Error (" + shadername + ")");
            }

            return shaderID;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to loadShader().", ex);
        }
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
        if (geometryShader != -1)
            glDetachShader(programID, geometryShader);

        glDeleteProgram(programID);
    }

    public void setInt(String uniformName, int value) {
        glUniform1i(getUniformLocation(uniformName), value);
    }

    public void setFloat(String uniformName, float value) {
        glUniform1f(getUniformLocation(uniformName), value);
    }

    public void setMatrix2f(String uniformName, Matrix2f matrix) {
        glUniformMatrix2(getUniformLocation(uniformName), true, Loader.loadBuffer(Matrix2f.store(matrix, new float[4])));
    }

    public void setMatrix3f(String uniformName, Matrix3f matrix) {
        glUniformMatrix3(getUniformLocation(uniformName), true, Loader.loadBuffer(Matrix3f.store(matrix, new float[9])));
    }

    public void setMatrix4f(String uniformName, Matrix4f matrix) {
        glUniformMatrix4(getUniformLocation(uniformName), true, Loader.loadBuffer(Matrix4f.store(matrix, new float[16])));
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
