import outskirts.client.main.Main;
import outskirts.util.CollectionUtils;
import outskirts.util.FileUtils;
import outskirts.util.ReflectionUtils;
import outskirts.util.SystemUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * tmp launcher class. simple setup and launch main program
 */
public final class Start {

    public static void main(String[] args) throws Exception {

        // DEFAULT: -ea  (java9+:) --add-opens java.base/jdk.internal.loader=ALL-UNNAMED
        // OSX: -XstartOnFirstThread -Djava.awt.headless=true -ea
        System.setProperty("org.lwjgl.librarypath", new File("libraries/platform/"+ SystemUtil.OS_NAME.toLowerCase()).getAbsolutePath());

        Files.list(Path.of("libraries"))
                .filter(p -> !Files.isDirectory(p) && p.endsWith(".jar"))
                .forEach(SystemUtil::addClasspath);

        Main.main(args);
    }

}
