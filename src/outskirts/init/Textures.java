package outskirts.init;

import outskirts.client.Loader;
import outskirts.client.render.Texture;
import outskirts.util.ResourceLocation;

// should uses EntityMaterials insteads indies Models and Textures ..?

public final class Textures {

    public static final Texture CONTAINER = loadTexture("materials/container2.png");
    public static final Texture CONTAINER_SPEC = loadTexture("materials/container2_specular.png");
    public static final Texture WOOD1 = loadTexture("materials/wood1.png");
    public static final Texture FLOOR = loadTexture("materials/floor.png");
    public static final Texture FRONT = loadTexture("materials/FRONT.png");

    public static final Texture GRASS = loadTexture("materials/mc/dirt.png");

    public static Texture BRICK = loadTexture("materials/brickwall.png");
    public static Texture BRICK_NORM = loadTexture("materials/brickwall_normal.png");

    public static Texture MAT_LERP_DET = loadTexture("materials/gray_mlerpd.png");

    private static Texture loadTexture(String s) {
        return Loader.loadTexture(new ResourceLocation(s).getInputStream());
    }

    static void init() {}
}
