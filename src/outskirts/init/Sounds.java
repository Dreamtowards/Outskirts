package outskirts.init;

import outskirts.client.Loader;
import outskirts.util.ResourceLocation;
import outskirts.util.Side;
import outskirts.util.SideOnly;

/**
 * int is OpenAL's Buffer_ID
 */
@SideOnly(Side.CLIENT)
public final class Sounds {

    public static final int GUI_CLICK = loadOGG("sounds/_click.ogg");


    private static int loadOGG(String resource) {
        return Loader.loadOGG(new ResourceLocation(resource).getInputStream());
    }

    static void init() {}
}
