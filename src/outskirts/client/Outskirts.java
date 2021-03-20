package outskirts.client;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import outskirts.client.audio.AudioEngine;
import outskirts.client.gui.Gui;
import outskirts.client.gui.debug.Gui1DNoiseVisual;
import outskirts.client.gui.debug.GuiDebugV;
import outskirts.client.gui.debug.GuiVert3D;
import outskirts.client.gui.ex.GuiIngame;
import outskirts.client.gui.ex.GuiRoot;
import outskirts.client.gui.screen.*;
import outskirts.client.main.TmpExtTest;
import outskirts.client.render.Camera;
import outskirts.client.render.renderer.RenderEngine;
import outskirts.entity.player.EntityPlayerSP;
import outskirts.entity.player.Gamemode;
import outskirts.event.client.WindowResizedEvent;
import outskirts.event.client.input.*;
import outskirts.init.Init;
import outskirts.mod.Mods;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.*;
import outskirts.util.concurrent.Scheduler;
import outskirts.util.profiler.Profiler;
import outskirts.util.vector.Vector3f;
import outskirts.world.WorldClient;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.opengl.GL11.*;
import static outskirts.client.ClientSettings.*;
import static outskirts.event.Events.EVENT_BUS;
import static outskirts.util.logging.Log.LOGGER;

public class Outskirts {

    // framebuffer coords.
    private float dWheel, mouseDX, mouseDY, mouseX, mouseY;
    private float ffdWheel, mouseFFDX, mouseFFDY;  // Full-Frame Delta value.
    private float width, height; // Framebuffer Coords. not GuiCoords.

    private static Outskirts INSTANCE;

    public static RenderEngine renderEngine;

    private AudioEngine audioEngine;

    private boolean running;

    private GuiRoot rootGUI = new GuiRoot();

    private WorldClient world;

    private Camera camera = new Camera();
    private GameTimer timer = new GameTimer();
    private RayPicker rayPicker = new RayPicker();

    private EntityPlayerSP player;

    private Thread thread = Thread.currentThread();
    private Scheduler scheduler = new Scheduler(thread);

    private Profiler profiler = new Profiler();

    private long numFrames;

    public void run() {
        try
        {
            this.startGame();

            while (this.running)
            {
                this.runGameLoop();
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            this.destroy();
        }
    }

    private void startGame() throws Throwable {

        // init, load Modules
        for (String e : ClientSettings.ProgramArguments.EXTENSIONS) {
            Mods.registerInit(new File(e));
        }

        this.running = true;
        Outskirts.INSTANCE = this;
        ClientSettings.loadOptions();
        this.createDisplay();

        renderEngine = new RenderEngine();
        audioEngine = new AudioEngine();

        Init.registerAll(Side.CLIENT);

        player = new EntityPlayerSP();
        player.setName("Player215");
        camera.setOwnerEntity(player);

        getRootGUI().addGui(GuiScreenMainMenu.INSTANCE);

        GuiScreenMainMenu.INSTANCE.addGui(new Gui1DNoiseVisual().exec(g -> {
            g.setX(30);
            g.setY(30);
            g.setWidth(800);
            g.setHeight(80);
        }));

        GuiIngame.INSTANCE.addGui(GuiDebugV.INSTANCE.exec(g -> g.setVisible(false)));
        GuiIngame.INSTANCE.addGui(GuiVert3D.INSTANCE.exec(g -> g.setVisible(false)));

        TmpExtTest.init();
        // Why stores HermiteData.? use for what.? what if just stores featurepoints.?
    }


    private void runGameLoop() throws Throwable { profiler.push("rt");

        timer.update();

        profiler.push("scheduleTasks");
        scheduler.processTasks();
        profiler.pop("scheduleTasks");

        profiler.push("runTick");
        while (timer.pollFullTick())
        {
            this.runTick();
        }
        profiler.pop("runTick");

        profiler.push("processInput");
        processInput();
        profiler.pop("processInput");

        if (world != null) {
            camera.update();
            rayPicker.update(camera.getPosition(), camera.getDirection());
        }

        // Render Phase
        profiler.push("render");
        {
            renderEngine.prepare();

            profiler.push("world");
            if (world != null) {
                renderEngine.render(world);
            }
            profiler.pop("world");

            profiler.push("gui");
            GuiRoot.refreshHovers();
            while (Gui.hasVolumeChanged()) {
                rootGUI.onLayout();
            }
            glDisable(GL_DEPTH_TEST);
            rootGUI.onDraw();
            glEnable(GL_DEPTH_TEST);
            profiler.pop("gui");
        }
        profiler.pop("render");

        profiler.pop("rt");
        profiler.push("updateDisplay");
        this.updateDisplay();
        profiler.pop("updateDisplay");
        numFrames++;
    }

    public static void setWorld(WorldClient world) {
        INSTANCE.world = world;
        if (world == null)
            return;

        RigidBody prb = getPlayer().getRigidBody();
        prb.transform().set(Transform.IDENTITY);
        prb.transform().origin.set(0,20,0);
        prb.getAngularVelocity().scale(0);
        prb.getLinearVelocity().scale(0);

        getPlayer().setGamemode(Gamemode.SPECTATOR);
        getPlayer().setFlymode(true);

    }

    private void runTick() {

        if (getWorld() != null) {
            if (isIngame()) {
                // MOVEMENT
                float lv =0.4f;
                if (Outskirts.isKeyDown(KEY_F))
                    lv *= 6;
                if (KEY_WALK_FORWARD.isKeyDown()) player.walk(lv, 0);
                if (KEY_WALK_BACKWARD.isKeyDown()) player.walk(lv, Maths.PI);
                if (KEY_WALK_LEFT.isKeyDown()) player.walk(lv, Maths.PI/2);
                if (KEY_WALK_RIGHT.isKeyDown()) player.walk(lv, -Maths.PI/2);
                if (KEY_JUMP.isKeyDown() && (player.isFlymode() || player.isOnGround())) player.walk(lv, new Vector3f(0, 1, 0));
                if (KEY_SNEAK.isKeyDown()) player.walk(lv, new Vector3f(0, -1, 0));

            }
            setMouseGrabbed(isIngame());


            profiler.push("world");
            world.onTick();
            profiler.pop("world");
        }
    }

    private void processInput() {
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

    private void destroy() {

        getRootGUI().removeAllGuis();  // dispatches Gui.DetachEvent.
        ClientSettings.saveOptions();

        Loader.destroy();
        audioEngine.destroy();

        Display.destroy();
    }

    public static boolean isIngame() {
        return Outskirts.getWorld() != null && Outskirts.getRootGUI().size() == 1 && Outskirts.getRootGUI().getGui(0) instanceof GuiIngame
                && !isAltKeyDown();
    }

    public static boolean isRunning() {
        return INSTANCE.running;
    }

    public static void shutdown() {
        assert isRunning();
        INSTANCE.running = false;
    }

    public static Profiler getProfiler() {
        return INSTANCE.profiler;
    }

    public static Scheduler getScheduler() {
        return INSTANCE.scheduler;
    }

    public static GuiRoot getRootGUI() {
        return INSTANCE.rootGUI;
    }

    public static EntityPlayerSP getPlayer() {
        return INSTANCE.player;
    }

    public static WorldClient getWorld() {
        return INSTANCE.world;
    }

    public static RayPicker getRayPicker() {
        return INSTANCE.rayPicker;
    }

    public static float getDelta() {
        return INSTANCE.timer.getDelta();
    }

    public static Camera getCamera() {
        return INSTANCE.camera;
    }

    public static long getSystemTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public static float getDWheel() {
        return INSTANCE.dWheel;
    }
    public static float getFFDWheel() {
        return INSTANCE.ffdWheel;
    }

    public static float getMouseDX() {
        return INSTANCE.mouseDX / GUI_SCALE;
    }
    public static float getMouseDY() {
        return INSTANCE.mouseDY / GUI_SCALE;
    }

    public static float getMouseFFDX() {
        return INSTANCE.mouseFFDX / GUI_SCALE;
    }
    public static float getMouseFFDY() {
        return INSTANCE.mouseFFDY / GUI_SCALE;
    }

    public static float getMouseX() {
        return INSTANCE.mouseX / GUI_SCALE;
    }
    public static float getMouseY() {
        return INSTANCE.mouseY / GUI_SCALE;
    }

    public static float getWidth() {
        return INSTANCE.width / GUI_SCALE;
    }
    public static float getHeight() {
        return INSTANCE.height / GUI_SCALE;
    }

    public static boolean isCtrlKeyDown() {
        return isKeyDown(KEY_LCONTROL) || isKeyDown(KEY_RCONTROL);
    }
    public static boolean isShiftKeyDown() {
        return isKeyDown(KEY_LSHIFT) || isKeyDown(KEY_RSHIFT);
    }
    public static boolean isAltKeyDown() {
        return isKeyDown(KEY_LMENU) || isKeyDown(KEY_RMENU);
    }

    public static boolean isMouseDown(int button) {
        return Mouse.isButtonDown(button);
    }
    public static boolean isKeyDown(int key) {
        return Keyboard.isKeyDown(key);
    }

    public static void setMouseGrabbed(boolean grabbed) {
        if (Mouse.isGrabbed() == grabbed) return;
        Mouse.setGrabbed(grabbed);
    }

    public static String getClipboard() {
        return SystemUtil.getClipboard();
    }
    public static void setClipboard(String s) {
        SystemUtil.setClipboard(s);
    }

    // framebuffer_size == window_size * os_content_scale == (guiCoords * GUI_SCALE)* os_content_scale
    public static int toFramebufferCoords(float guiCoords) {
        return (int)(guiCoords * GUI_SCALE);
    }

    private static BufferedImage screenshot(float gx, float gy, float gwidth, float gheight) {
        int wid=toFramebufferCoords(gwidth), hei=toFramebufferCoords(gheight), x=toFramebufferCoords(gx), y=toFramebufferCoords(getHeight()-gy-gheight);
        ByteBuffer pixels = BufferUtils.createByteBuffer(wid*hei*4);  // memAlloc(wid * hei * 4);
        glReadBuffer(GL_BACK);
        glReadPixels(x, y, wid,hei, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        return Loader.loadImage(pixels, wid, hei);
    }

    private void createDisplay() throws IOException, LWJGLException {

        ContextAttribs attribs = new ContextAttribs(3, 2)
                .withForwardCompatible(true).withProfileCore(true);

        Display.setResizable(true);
        Display.setTitle("DISPLAY");
        Display.setDisplayMode(new DisplayMode(ProgramArguments.WIDTH, ProgramArguments.HEIGHT));
        Display.create(new PixelFormat(), attribs);

        LOGGER.info("OperationSystem {} {}, rt {} {}, VM {} v{}",
                System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("java.version"), System.getProperty("os.arch"),
                System.getProperty("java.vm.name"), System.getProperty("java.vm.version"));
    }

    private void updateDisplay() {

        if (Display.isCloseRequested()) {  //glfwWindowShouldClose(window)
            Outskirts.shutdown();
        }

        if (Display.wasResized() || numFrames==0) {
            width = Display.getWidth();
            height = Display.getHeight();

            EVENT_BUS.post(new WindowResizedEvent());
            glViewport(0, 0, (int)width, (int)height);
        }

        Display.update();
        Display.sync(ClientSettings.FPS_CAPACITY);
    }


}
