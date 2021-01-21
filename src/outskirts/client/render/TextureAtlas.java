package outskirts.client.render;

import outskirts.client.Loader;
import outskirts.client.render.Texture;
import outskirts.util.vector.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * A big Texture that contains a bunch of Texture(s) and handling each Texture(s)'s TEX_COORDS/width/height
 */
public final class TextureAtlas {

    /** atlas texture */
    private Texture texture;

    private List<Fragment> atlasFragments = new ArrayList<>();

    public Texture getAtlasTexture() {
        return texture;
    }

    public void buildAtlas() {
        //calculate bound
        int totalWidth = 0;
        int maxHeight = 0;
        for (Fragment fragment : atlasFragments) {
            totalWidth += fragment.bufferedImage.getWidth();
            maxHeight = Math.max(fragment.bufferedImage.getHeight(), maxHeight);
        }

        //build atlas image
        BufferedImage atlasBufferedImage = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D)atlasBufferedImage.getGraphics();
        int startX = 0;
        for (Fragment fragment : atlasFragments) {
            g2d.drawImage(fragment.bufferedImage, startX, 0, null);
            startX += fragment.bufferedImage.getWidth();
        }

        //recalculate fragments TEX_COORDS info
        float tmpOffsetX = 0;
        for (Fragment fragment : atlasFragments) {
            float scaleX = (float) fragment.bufferedImage.getWidth() / totalWidth;
            float scaleY = (float) fragment.bufferedImage.getHeight() / maxHeight;

            fragment.OFFSET.set(tmpOffsetX, 1f - scaleY);
            fragment.SCALE.set(scaleX, scaleY);

            tmpOffsetX += scaleX;
        }

        //load build atlas texture
        this.texture = Loader.loadTexture(atlasBufferedImage);
    }

    public Fragment register(BufferedImage bufferedImage) {
        Fragment fragment = new Fragment(bufferedImage);
        atlasFragments.add(fragment);
        return fragment;
    }

    /**
     * at first, there has a field float[] "textureCoords", the field stores texCoords of the
     * AtlasFragment texture in AtlasTexture-Texture, but this field/way be cancelled., cause
     * use the fragment-textureCoords will being some problem, because the textureCoords is in a little
     * fragment, its not base 0, its base a uncertainly offset, so its not flexible data, you can't only use the
     * fragment-textureCoords make texture-scale/clipping because you not having offset data.
     * Now textureCoords is in-time build when section-model-build phase, so cancelled the texCoords field also
     * reduces unnecessary float[] data (not only improve flexibility)
     *
     * frag_textureCoordsVERT = TEX_COORDS_RECT * frag.SCALE.xy + frag.OFFSET.xy
     */
    public static class Fragment {

        private BufferedImage bufferedImage;
        public final Vector2f OFFSET = new Vector2f(-1, -1); //should be 0-1
        public final Vector2f SCALE = new Vector2f(-1, -1);  //should be 0-1

        private Fragment(BufferedImage bufferedImage) {
            this.bufferedImage = bufferedImage;
        }
    }

}