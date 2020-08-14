package outskirts.client.render.renderer.gui;

import outskirts.client.Loader;
import outskirts.client.gui.Gui;
import outskirts.client.material.Texture;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.renderer.gui.GuiRenderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.*;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector2i;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

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
            int off = textHeight / 10; // 8
            renderString(text, x+off, y+off, textHeight, Colors.BLACK40, false);
        }
        Vector2f pointer = new Vector2f(x, y);

        glActiveTexture(GL_TEXTURE0);

        for (int i = 0;i < text.length();i++) {
            char ch = text.charAt(i);

            float widthPercent = charWidth(ch);
            float displayWidth = textHeight*widthPercent;

            Texture tex = checkUnicodePageTexture(ch / 256);

            GuiRenderer.OP_colormul.set(color);
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
        int maxX = 0;
        int startX = 0;
        int startY = 0;
        for (int i = 0;i < texts.length();i++) {
            char ch = texts.charAt(i);
            float charWidth = charWidth(ch) * textHeight;
            startX += (int)charWidth + GAP_CHAR;
            if (ch == '\n') {
                maxX = Math.max(maxX, startX);
                startX=0;
                startY+=textHeight+GAP_LINE;
            }
        }
        startY += textHeight;
        maxX = Math.max(maxX, startX); // for supports single line. (no '\n')
        return new Vector2i(maxX, startY);
    }

    /**
     * calculate text index by text-display position.
     * @param pX,pY relative point on text.
     * @return [0, text.length] or say [0, max+1]
     */
    public int calculateTextIndex(String text, float textHeight, float pX, float pY) {
        int pointerX = 0;
        int pointerY = 0;
        float textsMaxPtrY = (StringUtils.count(text, "\n"))*(textHeight+GAP_LINE);
        for (int i = 0;i < text.length();i++) {
            char ch = text.charAt(i);
            float charWidth = charWidth(ch) * textHeight;

            if (pY < pointerY + textHeight + GAP_LINE || pointerY == textsMaxPtrY) { // "on the line" or on tail line.
                if (pointerX == 0 && pX < 0) return i; // line head
                else if (ch == '\n' && pX > pointerX) return i; // multi line tile
                else if (i==text.length()-1 && pX > pointerX + charWidth/2f) return i+1; // single line tail

                else if (pX >= pointerX && pX <= pointerX + charWidth/2) return i; // in curr char left-half
                else if (pX > pointerX + charWidth/2 && pX < pointerX + charWidth + GAP_CHAR) return i+1; // curr char right-half.
            }

            if (ch == '\n') {
                pointerX = 0;
                pointerY += textHeight + GAP_LINE;
            } else {
                pointerX += charWidth + GAP_CHAR;
            }
        }
        throw new RuntimeException("No possible exception.");
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
