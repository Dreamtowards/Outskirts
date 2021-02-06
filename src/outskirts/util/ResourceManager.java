package outskirts.util;

import outskirts.client.Loader;

import java.io.InputStream;

public final class ResourceManager {

    static InputStream getInputStream(String resdomain, String respath) {
        try {
            InputStream is = null;
            String path = "assets/" + resdomain + "/" + respath;

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
            if (is == null) {
                if (resdomain.equals("outskirts")) {
                    is = Loader.class.getResourceAsStream("/" + path);
                } else {
                    throw new RuntimeException("Could not locate the resource domain: "+resdomain);
//                    if (Mods.REGISTRY.containsKey(resource.getResourceDomain())) {
//                        JarFile modJarFile = new JarFile(Mods.REGISTRY.get(resource.getResourceDomain()).getFile());
//                        inputStream = modJarFile.getInputStream(modJarFile.getEntry(path));
//                    }
                }
            }

            return Validate.notNull(is, "Could not find the resource. %s", path);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
