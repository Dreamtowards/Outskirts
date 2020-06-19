package outskirts.extension;

import outskirts.util.registry.Registrable;

public abstract class Extension implements Registrable {

    private Extension.Manifest manifest;

    public final Manifest getManifest() {
        return manifest;
    }

    @Override
    public final String getRegistryID() {
        return getManifest().getID();
    }

    @Override
    public final <T extends Registrable> T setRegistryID(String registryID) {
        throw new UnsupportedOperationException("RegistryID already defined in manifest.");
    }

    public final class Manifest { // -name, -website, -authors[], -description

        private String id; // registryID / resourceDomain
        private String main;
        private String version;

        public String getID() {
            return id;
        }
    }
}
