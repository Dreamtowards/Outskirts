package outskirts.material;

import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;

public abstract class Material implements Registrable {

    public static final Registry<Material> REGISTRY = new Registry<>();

    private String registryID;


    @Override
    public String getRegistryID() {
        return registryID;
    }
}
