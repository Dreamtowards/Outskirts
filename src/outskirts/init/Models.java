package outskirts.init;

import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.util.Identifier;
import outskirts.util.Side;
import outskirts.util.SideOnly;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SideOnly(Side.CLIENT)
public final class Models {

    public static final Model STALL = loadOBJ("outskirts:materials/stall/stall.obj");

    public static final Model GEO_CUBE = loadOBJ("materials/geo/cube.obj"); // ModelRenderer.MODEL_CUBE;
    public static final Model GEO_SPHERE = loadOBJ("materials/geo/sphere.obj");
    public static final Model GEO_CYLINDER = loadOBJ("materials/geo/cylinder.obj");
    public static final Model GEO_CONE = loadOBJ("materials/geo/cone.obj");
    public static final Model GEOS_CAPSULE = loadOBJ("materials/_capsule.obj");// loadOBJ("materials/aya091/091_W_Aya_10K.obj");

//    private static Model loadMDL() {
//        try {
//            MDL.MDLData mdat = MDL.loadMDL(new FileInputStream("mdl-fts/aya091_10K.mdl"));
//            return Loader.loadModelTAN(mdat.indices, mdat.layouts.get(0), mdat.layouts.get(1), mdat.layouts.get(2));
//        } catch (FileNotFoundException ex) {
//            throw new RuntimeException(ex);
//        }
//    }

    private static Model loadOBJ(String resource) {
        return Loader.loadOBJ(new Identifier(resource).getInputStream());
    }

    static void init() {}
}
