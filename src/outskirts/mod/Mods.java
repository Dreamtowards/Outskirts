package outskirts.mod;

import outskirts.util.SystemUtil;
import outskirts.util.registry.Registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class Mods {

    public static final Registry<Mod> REGISTRY = new Registry<>();

    /**
     * register mod to the Registry. (not init)
     * @param modloc mod location. a Jar File(Specified) or a Directory Classpath(for Development)
     */
    public static void registerInit(Path modloc) {
        try {
            Mod.Manifest manifest = loadManifest(modloc);
            SystemUtil.addClasspath(modloc);

            Mod mod = (Mod)Class.forName(manifest.getMain()).newInstance();  // newInstance. loadClass. Init.
            mod.manifest = manifest;

            REGISTRY.register(mod);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load Mod \"" + modloc + "\".", ex);
        }
    }

    private static Mod.Manifest loadManifest(Path modloc) throws IOException {
        final String MANIFOLD_PATH = "META-INF/MODMETA.MF";  // MOD.MF  manifest.json

        if (Files.isDirectory(modloc)) {
            File dst = Path.of(modloc.toString(), MANIFOLD_PATH).toFile();
            return Mod.Manifest.parse(new Manifest(new FileInputStream(dst)).getMainAttributes());
        } else {  // File.
            JarFile jf = new JarFile(new File(modloc.toUri()));
            return Mod.Manifest.parse(
                    new Manifest(jf.getInputStream(jf.getEntry(MANIFOLD_PATH))).getMainAttributes()
            );
        }
    }

}
