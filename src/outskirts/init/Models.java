package outskirts.init;

import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.ResourceLocation;
import outskirts.util.Side;
import outskirts.util.SideOnly;

@SideOnly(Side.CLIENT)
public final class Models {

    public static final Model STALL = loadOBJ("outskirts:materials/stall/stall.obj");

    public static final Model GEO_CUBE = loadOBJ("materials/geo/cube.obj"); // ModelRenderer.MODEL_CUBE;
    public static final Model GEO_SPHERE = loadOBJ("materials/geo/sphere.obj");
    public static final Model GEO_CYLINDER = loadOBJ("materials/geo/cylinder.obj");
    public static final Model GEO_CONE = loadOBJ("materials/geo/cone.obj");

    private static Model loadOBJ(String resource) {
        return Loader.loadOBJ(new ResourceLocation(resource).getInputStream());
    }

    static void init() {}
}
