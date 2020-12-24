package outskirts.mod;

import outskirts.util.SystemUtils;
import outskirts.util.Validate;
import outskirts.util.logging.Log;
import outskirts.util.registry.Registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class Mods {

    public static final Registry<Mod> REGISTRY = new Registry<>();

    /**
     * register mod to the Registry. (not init)
     * @param modloc mod location. a Jar File(Specified) or a Directory Classpath(for Development)
     */
    public static void registerInit(File modloc) {
        try {
            Mod.Manifest manifest = loadManifest(modloc);
            SystemUtils.addClasspath(modloc);

            Mod mod = (Mod)Class.forName(manifest.getMain()).newInstance();
            mod.manifest = manifest;

            REGISTRY.register(mod);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load Mod \"" + modloc + "\".", ex);
        }
    }

    private static Mod.Manifest loadManifest(File modloc) throws IOException {
        final String MANIFOLD_PATH = "META-INF/MODMETA.MF";

        if (modloc.isFile()) {
            JarFile j = new JarFile(modloc);
            return Mod.Manifest.parse(
                    new Manifest(j.getInputStream(j.getEntry(MANIFOLD_PATH))).getMainAttributes()
            );
        } else if (modloc.isDirectory()) {
            return Mod.Manifest.parse(
                    new Manifest(new FileInputStream(new File(modloc, MANIFOLD_PATH))).getMainAttributes()
            );
        } else {
            throw new IllegalStateException();
        }
    }

}
