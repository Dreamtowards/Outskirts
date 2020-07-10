package outskirts.init;

import outskirts.client.Loader;
import outskirts.client.material.Texture;
import outskirts.util.ResourceLocation;
import outskirts.util.Side;

// should uses EntityMaterials insteads indies Models and Textures ..?

public final class Textures {

    public static final Texture CONTAINER = loadTexture("materials/container2.png");
    public static final Texture CONTAINER_SPEC = loadTexture("materials/container2_specular.png");
    public static final Texture WOOD1 = loadTexture("materials/wood1.png");
    public static final Texture FLOOR = loadTexture("materials/floor.png");
    public static final Texture FRONT = loadTexture("materials/FRONT.png");

    public static final Texture ICON_LIGHT = loadTexture("textures/gui/icon_light.png");

    private static Texture loadTexture(String s) {
        return Loader.loadTexture(new ResourceLocation(s).getInputStream());
    }

    static void init() {}
}
