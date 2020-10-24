package outskirts.block;

import outskirts.client.material.TextureAtlas;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;

public abstract class Block implements Registrable {

    public static Registry<Block> REGISTRY = new Registry.RegistrableRegistry<>();

    public static TextureAtlas TEXTURE_ATLAS = new TextureAtlas();

    private String registryID;

    public TextureAtlas.Fragment theTxFrag;




    @Override
    public String getRegistryID() {
        return registryID;
    }
}
