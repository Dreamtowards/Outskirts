import outskirts.client.main.Main;
import outskirts.util.CollectionUtils;
import outskirts.util.FileUtils;
import outskirts.util.SystemUtil;

import java.io.File;

/**
 * tmp launcher class. simple setup and launch main program
 */
public class Start {

    public static void main(String[] args) throws Exception {

        // DEFAULT: -ea
        // OSX: -XstartOnFirstThread -Djava.awt.headless=true -ea
        System.setProperty("org.lwjgl.librarypath", "libraries/platform/"+ SystemUtil.OS_NAME.toLowerCase());

        if (CollectionUtils.contains(args, "--tmploadlibs")) { //tmp arg
            for (File file : FileUtils.listFiles(new File("libraries"))) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    SystemUtil.addClasspath(file);
                }
            }
        }





        Main.main(args);
    }

}
