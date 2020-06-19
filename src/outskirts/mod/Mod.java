package outskirts.mod;

import outskirts.util.registry.Registrable;

import java.io.IOException;
import java.util.jar.Attributes;

public abstract class Mod implements Registrable {

    Mod.Manifest manifest;

    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public String getRegistryID() {
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

        static Manifest parse(java.util.jar.Attributes attr) throws IOException {
            Manifest manifest = new Manifest();

            manifest.id = attr.getValue("ID");
            manifest.main = attr.getValue("Main");
            manifest.version = attr.getValue("Version");
            manifest.description = attr.getValue("Description");

            return manifest;
        }
    }

}
