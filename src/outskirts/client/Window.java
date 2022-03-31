package outskirts.client;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import outskirts.client.ClientSettings.ProgramArguments;
import outskirts.event.Events;
import outskirts.event.client.WindowResizedEvent;
import outskirts.event.client.input.*;
import outskirts.util.KeyBinding;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static outskirts.event.Events.EVENT_BUS;
import static outskirts.util.logging.Log.LOGGER;

public final class Window {

    private long window;

    /* All these are Framebuffer Coordinate. */
    private float x;
    private float y;
    private float width;
    private float height;

    private float mouseX;
    private float mouseY;

    /* Frame-based Delta. not Event-based. don't use in event-call. */
    private float mouseDX;
    private float mouseDY;
    private float scrollDX;
    private float scrollDY;

    private String title;
    // resizable, fullscreen


    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public float getMouseX() { return mouseX; }
    public float getMouseY() { return mouseY; }
    public float getMouseDX() { return mouseDX; }
    public float getMouseDY() { return mouseDY; }
    public float getScrollDX() { return scrollDX; }
    public float getScrollDY() { return scrollDY; }
    public float getDScroll() { return scrollDX+scrollDY; }

    public void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW.");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);  // OSX req.
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
        glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);

        window = glfwCreateWindow(ProgramArguments.WIDTH, ProgramArguments.HEIGHT, title="DISPLAY", 0, 0);
        if (window == 0)
            throw new IllegalStateException("Failed to create the GLFW window.");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);  // 1: enable v-sync.
        GL.createCapabilities();  // GL Context.

        glfwShowWindow(window);
        glfwFocusWindow(window);

        initCallbacks();
        printSystemLog();

        int[] w = new int[1], h = new int[1];
        glfwGetFramebufferSize(window, w, h);
        width = w[0];
        height = h[0];
    }

    private boolean firstupdate = true;

    public void updateWindow() {
        if (firstupdate) {
            firstupdate = false;
            EVENT_BUS.post(new WindowResizedEvent());
        }
        resetFramebasedDeltas();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void destroy() {

        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void resetFramebasedDeltas() {  // call right before glfwPollEvents(), every frame.
        mouseDX = 0;
        mouseDY = 0;
        scrollDX = 0;
        scrollDY = 0;
    }

    public boolean isCloseRequested() {
        return glfwWindowShouldClose(window);
    }

    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(window, title);
    }
    public String getTitle() {
        return title;
    }
    public String getClipboard() {
        return glfwGetClipboardString(window);
    }
    public void setClipboard(String s) {
        glfwSetClipboardString(window, s);
    }

    public static double getHighResolutionTime() {
        return glfwGetTime();
    }


    public boolean isKeyDown(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }
    public boolean isMouseDown(int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

    public void setMousePos(float x, float y) {
        glfwSetCursorPos(window, x, y);
    }

    private boolean mouseGrabbed = false;
    public void setMouseGrabbed(boolean grabbed) {
        if (grabbed == mouseGrabbed) return;
        mouseGrabbed = grabbed;
        glfwSetInputMode(window, GLFW_CURSOR, grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    private void initCallbacks() {

        glfwSetFramebufferSizeCallback(window, this::glfwcallback_framebuffer_size);

        glfwSetKeyCallback(window, this::glfwcallback_key);
        glfwSetCharCallback(window, this::glfwcallback_char);

        glfwSetMouseButtonCallback(window, this::glfwcallback_mouse_button);
        glfwSetCursorPosCallback(window, this::glfwcallback_mouse_pos);
        glfwSetScrollCallback(window, this::glfwcallback_scroll);

    }

    private void glfwcallback_framebuffer_size(long w, int nw, int nh) {
        width = nw;
        height = nh;

        EVENT_BUS.post(new WindowResizedEvent());
        glViewport(0, 0, (int)width, (int)height);
    }

    private void glfwcallback_key(long w, int key, int scancode, int action, int mode) {
        boolean pressed = action==GLFW_PRESS;

        EVENT_BUS.post(new KeyboardEvent(key, pressed));
        KeyBinding.postInput(key, pressed, KeyBinding.TYPE_KEYBOARD);
    }

    private void glfwcallback_char(long w, int ch) {

        // if (ch >= ' ' && ch != 127)  // 127: backspace/DEL
        EVENT_BUS.post(new CharInputEvent(ch));
    }

    private void glfwcallback_mouse_button(long w, int button, int action, int mods) {
        boolean pressed = action==GLFW_PRESS;

        EVENT_BUS.post(new MouseButtonEvent(button, pressed));
        KeyBinding.postInput(button, pressed, KeyBinding.TYPE_MOUSE);
    }

    private void glfwcallback_mouse_pos(long w, double _xpos, double _ypos) {
        float xpos = (float)_xpos*2, ypos = (float)_ypos*2;
        float edx = xpos-mouseX, edy = ypos-mouseY;  // ED: Event-based Delta. not Frame-based. it might litter than frame-based, since one frame might have multiple events.

        mouseDX += edx;
        mouseDY += edy;
        mouseX = xpos;
        mouseY = ypos;

        EVENT_BUS.post(new MouseMoveEvent(edx, edy));
    }

    private void glfwcallback_scroll(long w, double _xoffset, double _yoffset) {
        float edx = (float)_xoffset, edy = (float)_yoffset;

        scrollDX += edx;
        scrollDY += edy;

        EVENT_BUS.post(new InputScrollEvent(edx, edy));
    }

    public static void printSystemLog() {

        LOGGER.info("OperationSystem {} {}, rt {} {}, VM {} v{}",
                System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("java.version"), System.getProperty("os.arch"),
                System.getProperty("java.vm.name"), System.getProperty("java.vm.version"));
    }


}
