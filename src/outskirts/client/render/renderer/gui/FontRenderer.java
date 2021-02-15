package outskirts.client.render.renderer.gui;

import outskirts.client.Loader;
import outskirts.client.gui.Gui;
import outskirts.client.render.Texture;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.*;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector4f;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec4;

public class FontRenderer extends Renderer {

    public static float OP_CHAR_GAP = 1;
    public static float OP_LINE_GAP = 2;

//    public static String _UNSPT_OP_FONT = null;
    private static Vector2f OP_DIRECTION = new Vector2f(Vector2f.UNIT_Y);

    /**
     * 0.0 - 1.0: from align_left to align_right. 0.5f is align_center
     */
    private static float OP_TEXT_ALIGNMENT = 0;

    /**
     * only contact with glyph_widths.bin for calculation. the Max-value in glyph_widths.bin.
     * for calculate char width/height ratio percent. (glyph_widths[unicode]/GLYPH_WIDTH_MAX=[0.0-1.0])
     */
    public static final int GLYPH_WIDTHS_MAXV = 255;
    private byte[] glyphWidths = new byte[65536];

    private Texture[] unicodePageTextures = new Texture[256];

    public FontRenderer() {
        // read glyph_widths
        try {
            IOUtils.readFully(new Identifier("font/glyph_widths.bin").getInputStream(), glyphWidths);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to read glyph_widths.bin");
        }
    }

    @Override
    public ShaderProgram getShader() {
        return null;
    }

    private Texture checkUnicodePageTexture(int unicodePage) {
        if (unicodePageTextures[unicodePage] == null) {
            unicodePageTextures[unicodePage] = Loader.loadTexture(new ResourceLocation(String.format("font/unicode_page_%s.png", unicodePage)).getInputStream());
        }
        return unicodePageTextures[unicodePage];
    }

    public void renderString(String text, float x, float y, float textHeight, Vector4f color, boolean renderShadow) {
        if (renderShadow) {
            float off = textHeight / 16; // 8
            float perc = .4f;
            renderString(text, x+off, y+off, textHeight, vec4(color).scale(perc,perc,perc, 1f), false);
        }

        glActiveTexture(GL_TEXTURE0);

        walkchars(text, textHeight, p -> {

            Texture tex = checkUnicodePageTexture(p.ch / 256);

            GuiRenderer.OP_colormul.set(color);
            Gui.drawTexture(tex, x+p.currX, y+p.currY, p.charWidth, textHeight, (p.ch%16)/16f, (float)((p.ch%256)/16)/16f, charWidth(p.ch)/16f , 1f/16f);

        });
    }

    /**
     * @return 0.0-1.0 percent of max-width
     */
    public float charWidth(char ch) {
        return (glyphWidths[ch] & 0xFF) / (float)GLYPH_WIDTHS_MAXV;
    }

    public WalkerParams walkchars(String text, float textHeight, Consumer<WalkerParams> visitor, int toIndex) {
        Vector2f bound = calculateBound(text, textHeight);
        WalkerParams p = new WalkerParams();
        p.currX=0;p.currY=0;
        {   // init-currX is required. in somewhere other place, from the WalkerParams p return out...  e.g. text position calc.
            int eol = text.indexOf('\n'); // index: tail of the line.
            p.currX = (bound.x-calculateBound(text.substring(0, eol==-1?text.length(): eol), textHeight).x) * OP_TEXT_ALIGNMENT; }
        for (p.index = 0;p.index < toIndex;p.index++) {
            p.ch = text.charAt(p.index);
            p.charWidth = charWidth(p.ch) * textHeight;

            visitor.accept(p);

            p.currX += p.charWidth + OP_CHAR_GAP;
            if (p.ch == '\n') {
                int eol = text.indexOf('\n', p.index+1); // index: tail of the line.
                p.currX = (bound.x-calculateBound(text.substring(p.index+1, eol==-1?text.length(): eol), textHeight).x) * OP_TEXT_ALIGNMENT;
                p.currY += textHeight + OP_LINE_GAP;
            }
        }
        return p;
    }
    public WalkerParams walkchars(String text, float textHeight, Consumer<WalkerParams> visitor) {
        return walkchars(text, textHeight, visitor, text.length());
    }

    public Vector2f calculateBound(String text, float textHeight) {
        float currX=0, currY=0, maxX=0;
        for (int i = 0;i < text.length();i++) {
            char ch = text.charAt(i);
            currX += charWidth(ch)*textHeight + OP_CHAR_GAP;
            if (ch == '\n' || i==text.length()-1) {
                maxX = Math.max(maxX, currX);
                currX=0;
                currY += textHeight+OP_LINE_GAP;
            }
        }
        return new Vector2f(maxX, currY-OP_LINE_GAP);
    }

    /**
     * calculate text index by text-display position.
     * @param pointX,pointY relative point on text.
     * @return [0, text.length] or say [0, max+1]
     */
    public int calculateTextIndex(String text, float textHeight, float pointX, float pointY) {
        AtomicInteger tIdx =  new AtomicInteger(-1);
        walkchars(text, textHeight, p -> {
            if (tIdx.get() != -1)
                return;
            boolean isTailLine = text.lastIndexOf('\n') < p.index;
            if (pointY < p.currY + textHeight + OP_LINE_GAP || isTailLine) { // point "on/before"(in/upward) the line. or on the tail line.
                if (pointX < p.currX + p.charWidth/2f) // everychar left/char_mid
                    tIdx.set(p.index);
                if (p.ch=='\n' && pointX > p.currX) // multi-line tail
                    tIdx.set(p.index);
                else if (p.index==text.length()-1 && pointX >= p.currX+p.charWidth/2f) // single line tile, select tail char right-side.
                    tIdx.set(text.length());
            }
        });
        if (tIdx.get() == -1) // when empty text. or..
            return text.length();
//        assert tIdx.get() != -1;
        return tIdx.get();
    }

    /**
     * @param index [0, text.length]
     */
    public Vector2f calculateTextPosition(String text, int textHeight, int index, Vector2f out) {
        WalkerParams params =
                walkchars(text, textHeight, p -> {}, index);
        return out.set(params.currX, params.currY);
    }
    
    public static final class WalkerParams {
        public int index;
        public float currX, currY;
        public float charWidth;
        public char ch;
    }
}
