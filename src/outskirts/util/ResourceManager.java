package outskirts.util;

import outskirts.client.GameSettings;
import outskirts.client.Loader;

import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ResourceManager {

    static InputStream getInputStream(String resourceDomain, String resourcePath) {
        try {
            InputStream inputStream = null;
            String path = "assets/" + resourceDomain + "/" + resourcePath;

//            //ResourcePacks
//            for (int i = GameSettings.RESOURCE_PACKS.size() - 1; i >= 0; i--) {
//                if (inputStream == null) {
//                    ZipFile zipFile = GameSettings.RESOURCE_PACKS.get(i);
//                    ZipEntry zipPathEntry = zipFile.getEntry(path);
//                    if (zipPathEntry != null) {
//                        inputStream = zipFile.getInputStream(zipPathEntry);
//                    }
//                }
//            }

            //jar assets
            if (inputStream == null) {
                if (resourceDomain.equals("outskirts")) {
                    inputStream = Loader.class.getResourceAsStream("/" + path);
                } else {
                    throw new RuntimeException("unsupport domain");
//                    if (Mods.REGISTRY.containsKey(resource.getResourceDomain())) {
//                        JarFile modJarFile = new JarFile(Mods.REGISTRY.get(resource.getResourceDomain()).getFile());
//                        inputStream = modJarFile.getInputStream(modJarFile.getEntry(path));
//                    }
                }
            }

            return Validate.notNull(inputStream, "Could not find the resource. %s", path);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
