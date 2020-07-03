package outskirts.client.animation.loader.dae;

import outskirts.util.XML;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Unsupported:
 * <asset>, <library_cameras>, <library_lights>, <library_images>, <library_materials>, <library_effects>, <scene>, <extra>.
 *
 * Loadup part:
 * <library_geometries>, <library_controllers>, <library_visual_scenes>, <library_animations>.
 */
public class DaeLoader {

    private static final List<String> SUPPORTED_VERSIONS = Arrays.asList("1.4.0", "1.4.1");

    public static DaeData loadDAE(InputStream inputStream) {
        XML COLLADA = new XML(inputStream);
        assert SUPPORTED_VERSIONS.contains(COLLADA.getAttributes().get("version")) : "Unsupported DAE Version.";

        for (XML child : COLLADA.getChildren()) {
            switch (child.getName()) {
                case "library_geometries":

                    break;
                case "library_controllers":

                    break;
                case "library_visual_scenes":

                    break;
                case "library_animations":

                    break;
            }
            Log.LOGGER.info(child.getName());
        }

        return null;
    }

    public static final class DaeData {



    }

}
