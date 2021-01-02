import net.java.games.input.*;
import outskirts.client.main.Main;
import outskirts.mod.Mods;
import outskirts.util.CollectionUtils;
import outskirts.util.FileUtils;
import outskirts.util.HttpUtils;
import outskirts.util.SystemUtils;
import outskirts.util.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * tmp launcher class. simple setup and launch main program
 */
public class Start {

    public static void main(String[] args) throws Exception {

        // DEFAULT: -ea
        // OSX: -XstartOnFirstThread -Djava.awt.headless=true
        System.setProperty("java.library.path", "libraries/platform/"+SystemUtils.OS_NAME.toLowerCase());

        if (CollectionUtils.contains(args, "--tmploadlibs")) { //tmp arg
            for (File file : FileUtils.listFiles(new File("libraries"))) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    SystemUtils.addClasspath(file);
                }
            }
        }





        Main.main(args);
    }

}
