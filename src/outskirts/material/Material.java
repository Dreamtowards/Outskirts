package outskirts.material;

import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;

public abstract class Material implements Registrable {

    public static final Registry<Material> REGISTRY = new Registry<>();

    private String registryID;

    private float hardness = 1;

//    private boolean translucent = false;

    /**
     * Leaf
     * TallGrass
     * Fluid Water
     * Sand
     */

    public Material() {
    }

    protected final void setHardness(float hardness) { this.hardness = hardness; }
    public final float getHardness() { return hardness; }

//    protected void setTranslucent(boolean translucent) { this.translucent = translucent; }
//    public boolean isTranslucent() { return translucent; }

    @Override
    public String getRegistryID() {
        return registryID;
    }
}
