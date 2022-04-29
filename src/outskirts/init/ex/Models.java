package outskirts.init.ex;

import outskirts.client.Loader;
import outskirts.client.render.Model;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.Identifier;
import outskirts.util.Side;
import outskirts.util.SideOnly;

@SideOnly(Side.CLIENT)
public final class Models {

//    public static final Model STALL = loadOBJ("outskirts:materials/stall/stall.obj");

    public static final Model EMPTY = Loader.loadModel(new VertexBuffer());
    public static final Model GEO_CUBE = loadOBJ("entity/geo/cube.obj"); // ModelRenderer.MODEL_CUBE; //
    public static final Model GEO_SPHERE = loadOBJ("entity/geo/sphere.obj");
//    public static final Model GEO_CYLINDER = loadOBJ("materials/geo/cylinder.obj");
//    public static final Model GEO_CONE = loadOBJ("materials/geo/cone.obj");
//    public static final Model GEOS_CAPSULE = loadOBJ("elements/entity/geo/_capsule.obj");// loadOBJ("materials/aya091/091_W_Aya_10K.obj");



    private static Model loadOBJ(String resource) {
        return Loader.loadOBJ(new Identifier(resource).getInputStream());
    }

    public static void init() {}
}
