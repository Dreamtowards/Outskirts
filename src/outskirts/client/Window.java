package outskirts.client;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import outskirts.event.client.WindowResizedEvent;
import outskirts.event.client.input.*;
import outskirts.util.KeyBinding;
import outskirts.util.SystemUtil;

import java.io.IOException;

import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.opengl.GL11.glViewport;
import static outskirts.event.Events.EVENT_BUS;
import static outskirts.util.logging.Log.LOGGER;

public final class Window {

    /* All these are Framebuffer Coordinate. */
    private float x;
    private float y;
    private float width;
    private float height;

    private float mouseX;
    private float mouseY;

    /* Event-based Delta. */  // todo: why not treat DX as FFDX? since there almost use base on full-frame
    private float mouseDX;
    private float mouseDY;
    private float dWheel;  // todo: scroll shoud also have x and y for e.g. touchpad support. and provide getDScroll() => x+y; for universal use.

    /* Full-Frame based Delta. */
    private float mouseFFDX;
    private float mouseFFDY;
    private float ffdWheel;

    // private String title;
    // resizable, fullscreen

    private boolean firstUpdate = true;



    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public float getMouseX() { return mouseX; }
    public float getMouseY() { return mouseY; }
    public float getMouseDX() { return mouseDX; }
    public float getMouseDY() { return mouseDY; }
    public float getDWheel() { return dWheel; }  // getDWheel() = combination of wheelDX+wheelDY.

    public float getMouseFFDX() { return mouseFFDX; }
    public float getMouseFFDY() { return mouseFFDY; }
    public float getFFDWheel() { return ffdWheel; }

    public void initWindow() throws IOException, LWJGLException {

        ContextAttribs attribs = new ContextAttribs(3, 2)
                .withForwardCompatible(true).withProfileCore(true);

        Display.setResizable(true);
        Display.setTitle("DISPLAY");
        Display.setDisplayMode(new DisplayMode(ClientSettings.ProgramArguments.WIDTH, ClientSettings.ProgramArguments.HEIGHT));
        Display.create(new PixelFormat(), attribs);

        LOGGER.info("OperationSystem {} {}, rt {} {}, VM {} v{}",
                System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("java.version"), System.getProperty("os.arch"),
                System.getProperty("java.vm.name"), System.getProperty("java.vm.version"));
    }


    public void processInput() {
        dWheel = 0; mouseDX = 0; mouseDY = 0;
        ffdWheel = Mouse.getDWheel();
        mouseFFDX = Mouse.getDX();
        mouseFFDY = -Mouse.getDY();

        while (Mouse.next()) {
            if (Mouse.getEventButton() == -1) {
                dWheel = Mouse.getEventDWheel() / 80f;
                mouseDX =  Mouse.getEventDX();
                mouseDY = -Mouse.getEventDY();
                mouseX = Mouse.getEventX();
                mouseY = Display.getHeight()-Mouse.getEventY();

                EVENT_BUS.post(new MouseMoveEvent(mouseDX, mouseDY));
                EVENT_BUS.post(new MouseWheelEvent(dWheel));  // needs reduce.?
            } else {

                EVENT_BUS.post(new MouseButtonEvent(Mouse.getEventButton(), Mouse.getEventButtonState()));
                KeyBinding.postInput(Mouse.getEventButton(), Mouse.getEventButtonState(), KeyBinding.TYPE_MOUSE);
            }
        }

        while (Keyboard.next()) {

            EVENT_BUS.post(new KeyboardEvent(Keyboard.getEventKey(), Keyboard.getEventKeyState()));
            KeyBinding.postInput(Keyboard.getEventKey(), Keyboard.getEventKeyState(), KeyBinding.TYPE_KEYBOARD);

            char ch = Keyboard.getEventCharacter();
            if (Keyboard.getEventKeyState() && ch >= ' ' && ch != 127) {  // 127: backspace/DEL
                int k = Keyboard.getEventKey();
                if (SystemUtil.IS_OSX && (k==KEY_LEFT || k==KEY_UP || k==KEY_RIGHT || k==KEY_DOWN))
                    continue;
                EVENT_BUS.post(new CharInputEvent(ch));
            }
        }
    }

    public void updateWindow() {

        if (Display.wasResized() || firstUpdate) {
            width = Display.getWidth();
            height = Display.getHeight();

            EVENT_BUS.post(new WindowResizedEvent());
            glViewport(0, 0, (int)width, (int)height);
        }
        firstUpdate = false;

        Display.update();
        Display.sync(ClientSettings.FPS_CAPACITY);
    }

    public boolean isCloseRequested() {
        return Display.isCloseRequested();  //glfwWindowShouldClose(window)
    }


}
