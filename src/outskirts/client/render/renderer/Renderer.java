package outskirts.client.render.renderer;

import outskirts.client.render.shader.ShaderProgram;

/**
 * for the base class:
 * Inheritance always makes more complexity, but in this case,
 * Renderer just mark/collect/classification related renderer(s), this make Renderer system more Clear and Specification
 *
 * for Renderer:
 * Renderer is a rendering dispatcher/manager for a special render type/target. they process how to makes render.
 */
public abstract class Renderer {






    // Utility methods

    public static String[] createUniformNameArray(String uniformName, int length) {
        String[] nameArray = new String[length];
        for (int i = 0;i < length;i++) {
            nameArray[i] = String.format(uniformName, i);
        }
        return nameArray;
    }

}
