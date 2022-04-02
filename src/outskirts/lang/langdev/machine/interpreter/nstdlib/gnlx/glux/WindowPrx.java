package outskirts.lang.langdev.machine.interpreter.nstdlib.gnlx.glux;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import outskirts.lang.langdev.machine.interpreter.GObject;
import outskirts.lang.langdev.machine.interpreter.nstdlib._nstdlib;
import outskirts.util.SystemUtil;

import java.io.File;

public class WindowPrx {

    public static void doREG(_nstdlib.Reg r) {

        System.setProperty("org.lwjgl.librarypath", new File("libraries/platform/"+ SystemUtil.OS_NAME.toLowerCase()).getAbsolutePath());


        r.reg("nstdlib/gnlx/glux/window::init", f -> {
            try {
                Display.setDisplayMode(new DisplayMode(100, 100));
                Display.create();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        });

        r.reg("nstdlib/gnlx/glux/window::is_close_requested", f -> {
            f.retv(GObject.ofBoolean(Display.isCloseRequested()));
        });

    }

}
