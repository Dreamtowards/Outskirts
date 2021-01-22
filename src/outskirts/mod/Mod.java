package outskirts.mod;

import outskirts.util.registry.Registrable;

public abstract class Mod implements Registrable {

    Mod.Manifest manifest;

    public final Manifest getManifest() {
        return manifest;
    }

    @Override
    public final String getRegistryID() {
        return getManifest().getID();
    }

    public static class Manifest {

        /** registryID/resourceDomain */
        private String id;
        private String main;
        private String version;
        private String description;

        public String getID() {
            return id;
        }
        public String getMain() {
            return main;
        }
        public String getVersion() {
            return version;
        }
        public String getDescription() {
            return description;
        }

        static Manifest parse(java.util.jar.Attributes attr) {
            Manifest manifest = new Manifest();

            manifest.id = attr.getValue("ID");
            manifest.main = attr.getValue("Main");
            manifest.version = attr.getValue("Version");
            manifest.description = attr.getValue("Description");

            return manifest;
        }
    }

}
