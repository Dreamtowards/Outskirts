package outskirts.client.render;

import outskirts.client.Loader;
import outskirts.util.BitmapImage;
import outskirts.util.vector.Vector2f;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A big Texture that contains a bunch of Texture(s) and handling each Texture(s)'s TEX_COORDS/width/height
 */
public final class TextureAtlas {

    /** atlas texture */
    private Texture texture;

    private List<AtlasFragment> atlas = new ArrayList<>();

    public Texture getAtlasTexture() {
        return texture;
    }

    public void buildAtlas() {
        //calculate bound
        int totalWidth = 0;
        int maxHeight = 0;
        for (AtlasFragment fragment : atlas) {
            totalWidth += fragment.img.getWidth();
            maxHeight = Math.max(fragment.img.getHeight(), maxHeight);
        }

        //build atlas image
        BitmapImage atlasimg = new BitmapImage(totalWidth, maxHeight);
        int startX = 0;
        for (AtlasFragment fragment : atlas) {
            atlasimg.copyPixels(fragment.img, startX, 0);
            startX += fragment.img.getWidth();
        }

        //recalculate fragments TEX_COORDS info
        float tmpOffsetX = 0;
        for (AtlasFragment fragment : atlas) {
            float scaleX = (float) fragment.img.getWidth() / totalWidth;
            float scaleY = (float) fragment.img.getHeight() / maxHeight;

            fragment.OFFSET.set(tmpOffsetX, 1f - scaleY);
            fragment.SCALE.set(scaleX, scaleY);

            tmpOffsetX += scaleX;
        }

        //load build atlas texture
        this.texture = Loader.loadTexture(atlasimg);
        Loader.savePNG(atlasimg, Path.of("atlas.png"));
    }

    public List<AtlasFragment> fragments() {
        return Collections.unmodifiableList(atlas);
    }

    public AtlasFragment register(BitmapImage img) {
        AtlasFragment fragment = new AtlasFragment(img);
        atlas.add(fragment);
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
    public static class AtlasFragment {

        private BitmapImage img;
        public final Vector2f OFFSET = new Vector2f(-1, -1); //should be 0-1
        public final Vector2f SCALE = new Vector2f(-1, -1);  //should be 0-1

        private AtlasFragment(BitmapImage img) {
            this.img = img;
        }
    }

}
