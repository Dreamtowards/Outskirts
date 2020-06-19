package outskirts.client.render.renderer;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.*;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector2i;
import outskirts.util.vector.Vector4f;
import sun.tracing.dtrace.DTraceProviderFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

public class FontRenderer extends Renderer {

    public static int GAP_CHAR = 1;
    private static int GAP_LINE = 3;

    /**
     * only contact with glyph_widths.bin for calculation
     * char percent width (glyph_widths[unicode]/GLYPH_WIDTH_MAX=[0.0-1.0])
     */
    public static final int GLYPH_WIDTH_MAX = 255;
    private byte[] glyphWidths = new byte[65536];

    private Texture[] unicodePageTextures = new Texture[256];

    public FontRenderer() {
        // read glyph_widths
        try {
            new ResourceLocation("font/glyph_widths.bin").getInputStream().read(glyphWidths);
        } catch (Exception ex) {
            throw new RuntimeException("failed to read glyph_widths.bin");
        }
    }

    @Override
    public ShaderProgram getShader() {
        return null;
    }

    public Texture checkUnicodePageTexture(int unicodePage) {
        if (unicodePageTextures[unicodePage] == null) {
            unicodePageTextures[unicodePage] = Loader.loadTexture(new ResourceLocation(String.format("font/unicode_page_%s.png", unicodePage)).getInputStream());
        }
        return unicodePageTextures[unicodePage];
    }

    public void renderString(String text, float x, float y, int textHeight, Vector4f color, boolean renderShadow) {
        if (renderShadow) {
            int off = textHeight / 8;
            renderString(text, x+off, y+off, textHeight, Colors.BLACK40, false);
        }
        Vector2f pointer = new Vector2f(x, y);

        glActiveTexture(GL_TEXTURE0);

        for (int i = 0;i < text.length();i++) {
            char ch = text.charAt(i);

            float widthPercent = charWidth(ch);
            float displayWidth = textHeight*widthPercent;

            Texture tex = checkUnicodePageTexture(ch / 256);

            GuiRenderer.PARAM_colorMultiply.set(color);
            Gui.drawTexture(tex, pointer.x, pointer.y, displayWidth, textHeight, (ch%16)/16f, (float)((ch%256)/16)/16f, 1f/16f*widthPercent , 1f/16f);

            pointer.x += (int)displayWidth + GAP_CHAR;

            if (ch == '\n') {
                pointer.x = x;
                pointer.y += textHeight + GAP_LINE;
            }
        }

    }

    /**
     * @return 0.0-1.0 percent of max-width
     */
    public float charWidth(char ch) {
        return (glyphWidths[ch] & 0xFF) / (float)GLYPH_WIDTH_MAX;
    }

    public Vector2i calculateBound(String texts, int textHeight) {
        int startX = 0;
        int startY = 0;
        for (int i = 0;i < texts.length();i++) {
            char ch = texts.charAt(i);

            float widthRatio = charWidth(ch);

            startX += (int)(widthRatio * textHeight) + GAP_CHAR;
        }
        startY += textHeight;
        return new Vector2i(startX, startY);
    }

    /**
     * calculate text index by text-display position.
     * @param posX,posY relative point position on text.
     * @return 0 - text.length [0, max+1]
     */
    public int calculateTextIndex(String text, float textHeight, float posX, float posY) {
        int pointerX = 0;
        int pointerY = 0;
        for (int i = 0;i < text.length();i++) {
            char ch = text.charAt(i);

            float charWidth = charWidth(ch) * textHeight;

            if (posY >= pointerY && posY < pointerY + textHeight + GAP_LINE) { // in curr line
                if (posX >= pointerX && posX <= pointerX + charWidth/2) { // in curr char left-half
                    return i;
                } else if (posX > pointerX + charWidth/2 && posX < pointerX + charWidth + GAP_CHAR) {
                    return i+1;
                }
            }

            if (ch == '\n') {
                pointerX = 0;
                pointerY += textHeight + GAP_LINE;
            } else {
                pointerX += charWidth + GAP_CHAR;
            }
        }
        return -1;
    }

    /**
     * @param j 0-text.length
     */
    public Vector2i calculateTextPosition(String text, int textHeight, int j, Vector2i dest) {
        if (dest == null)
            dest = new Vector2i();
        int pointerX = 0;
        int pointerY = 0;
        for (int i = 0;i < j;i++) {
            char ch = text.charAt(i);

            if (ch == '\n') {
                pointerX = 0;
                pointerY += textHeight + GAP_LINE;
            } else {
                int charWidth = (int)(charWidth(ch) * textHeight);
                pointerX += charWidth + GAP_CHAR;
            }
        }
        return dest.set(pointerX, pointerY);
    }
}
