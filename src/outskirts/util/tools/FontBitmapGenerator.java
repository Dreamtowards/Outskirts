package outskirts.util.tools;

import outskirts.client.render.renderer.FontRenderer;
import outskirts.util.FileUtils;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FontBitmapGenerator {

    public static void main(String[] args) throws Exception {

        Font font = new Font("Arial", Font.PLAIN, 0);

        byte[] glyphs = new byte[65536];

        FileUtils.mkdirs(new File("Fonts"));

        for (int page = 0;page < 256;page++) {
            Log.info("page " + page);

            BufferedImage bufferedImage = generatePage(font, (char)(page * 256), 512, glyphs);

            ImageIO.write(bufferedImage, "PNG", new File("Fonts/unicode_page_" + page + ".png"));
        }

        IOUtils.write(new ByteArrayInputStream(glyphs), new FileOutputStream("Fonts/glyph_widths.bin"));

    }


    public static BufferedImage generatePage(Font font, char index, int resolution, byte[] glyphs) {
        int PAGE_SIZE = 16; //how many chars one column/row
        int charSize = resolution / PAGE_SIZE;

        BufferedImage bufferedImage = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)bufferedImage.getGraphics();

        g.setFont(font.deriveFont((float)charSize));

        FontMetrics metrics = g.getFontMetrics();
        g.setFont(font.deriveFont(
                ((float)charSize / (metrics.getAscent() + metrics.getDescent())) * charSize
        ));
        metrics = g.getFontMetrics();

        //drawString()'s y is in baseline
        //Ascent  is top-tp-baseline
        //Descent is bottom-to-baseline

        int counter = 0;
        for (int startY = 0;startY<resolution;startY += charSize) {
            for (int startX = 0;startX < resolution;startX += charSize) {
                char ch = (char)(index + counter);

                g.drawString(Character.toString(ch), startX, startY + metrics.getAscent());

                float widthRatio = (float) metrics.charWidth(ch) / charSize;
                glyphs[ch] = (byte)((int)(widthRatio * FontRenderer.GLYPH_WIDTH_MAX) & 0xFF);

                counter++;
            }
        }

        return bufferedImage;
    }

}
