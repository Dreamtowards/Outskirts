package outskirts.client;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Platform;
import outskirts.client.audio.AudioEngine;
import outskirts.client.gui.Gui;
import outskirts.client.gui.debug.Gui1DNoiseVisual;
import outskirts.client.gui.debug.GuiDebugV;
import outskirts.client.gui.ex.GuiIngame;
import outskirts.client.gui.ex.GuiRoot;
import outskirts.client.gui.screen.*;
import outskirts.client.main.TmpExtTest;
import outskirts.client.render.Camera;
import outskirts.client.render.Texture;
import outskirts.client.render.renderer.RenderEngine;
import outskirts.entity.player.EntityPlayerSP;
import outskirts.entity.player.Gamemode;
import outskirts.init.Init;
import outskirts.mod.Mods;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.*;
import outskirts.util.concurrent.Scheduler;
import outskirts.util.logging.Log;
import outskirts.util.profiler.Profiler;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;
import outskirts.world.WorldClient;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static outskirts.client.ClientSettings.*;
import static outskirts.util.logging.Log.LOGGER;

public class Outskirts {

    private static Outskirts INST;

    public static RenderEngine renderEngine;
    private AudioEngine audioEngine;

    private WorldClient world;
    private EntityPlayerSP player;

    private boolean running;
    private GuiRoot rootGUI = new GuiRoot();
    private Window window = new Window();
    private Camera camera = new Camera();
    private GameTimer timer = new GameTimer();
    private RayPicker rayPicker = new RayPicker();
    private Scheduler scheduler = new Scheduler(Thread.currentThread());
    private Profiler profiler = new Profiler();

    public void run() {
        try
        {
            this.start();

            while (this.running)
            {
                this.runMainLoop();
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

    private void start() throws Throwable {

        for (String p : ProgramArguments.MODS) {  // mods load, init.
            Mods.registerInit(Path.of(p));
        }

        this.running = true;
        Outskirts.INST = this;
        ClientSettings.loadOptions();
        window.initWindow();
        printSystemLog();

        renderEngine = new RenderEngine();
        audioEngine = new AudioEngine();

        window.postInitialGlfwEvents();
        window.makeWindowCentered();
        Init.registerAll(Side.CLIENT);



        player = new EntityPlayerSP();
        player.setName("Entitypl0xEF");
        camera.setOwnerEntity(player);

//        getRootGUI().addGui(GuiIngame.INSTANCE);
        getRootGUI().addGui(GuiScreenMainMenu.INSTANCE.exec(g -> {
            g.addOnDrawListener(e -> {
                Gui.drawRect(Colors.BLACK40, rootGUI);
            });
        }));

        GuiScreenMainMenu.INSTANCE.addGui(new Gui1DNoiseVisual().exec(g -> {
            g.setX(30);
            g.setY(30);
            g.setWidth(800);
            g.setHeight(80);
        }));



        TmpExtTest.init();
        // Why stores HermiteData.? use for what.? what if just stores featurepoints.?
    }


    private void runMainLoop() throws Throwable { profiler.push("rt");

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

        // Render Phase
        profiler.push("render");
        {
            renderEngine.prepare();

            profiler.push("world");
            if (world != null) {
                camera.update();
                rayPicker.update(camera.getPosition(), camera.getDirection());

                renderEngine.render(world);
            }
            profiler.pop("world");
            profiler.push("gui");
            this.renderGUI();
            profiler.pop("gui");
        }
        profiler.pop("render");

        profiler.pop("rt");
        profiler.push("updateDisplay");
        window.updateWindow();
        profiler.pop("updateDisplay");

        if (window.isCloseRequested())
            Outskirts.shutdown();

        if (GuiDebugV.INSTANCE.isVisible())
            Gui.requestDraw();
//        Log.LOGGER.info("FPS "+(1f/getDelta()));
    }

    private void renderGUI() {
        if (!isIngame())
            GuiRoot.updateHovers();

        int tmp = 0;
        while (Gui.layoutRequested) {  // if or while?
            Gui.layoutRequested = false;
            rootGUI.onLayout();

            Gui.requestDraw();
            if (tmp++ > 5) {
                LOGGER.error("Error on Gui layouting: over iterated. skipped.");
                break;
            }
        }
        if (Gui.drawRequested) {
            Gui.drawRequested = false;
            renderEngine.fbGUI.pushFramebuffer();
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT);

            glDisable(GL_DEPTH_TEST);
            rootGUI.onDraw();
            glEnable(GL_DEPTH_TEST);

            renderEngine.fbGUI.popFramebuffer();
        }
        glDisable(GL_DEPTH_TEST);
        Gui.drawTexture(renderEngine.fbGUI.colorTextures(0), rootGUI);
        glEnable(GL_DEPTH_TEST);
    }

    private void runTick() {

        if (getWorld() != null) {
            if (isIngame()) {
                // MOVEMENT
                float lv =1.4f;
                if (Outskirts.isKeyDown(GLFW.GLFW_KEY_F))
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

    public static void setWorld(WorldClient world) {
        INST.world = world;
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

    private void destroy() {

        getRootGUI().removeAllGuis();  // dispatches Gui.DetachEvent.
        ClientSettings.saveOptions();

        Loader.destroy();
        audioEngine.destroy();

        window.destroy();
    }

    public static boolean isIngame() {
        return Outskirts.getWorld() != null && Outskirts.getRootGUI().count() == 1 && Outskirts.getRootGUI().getGui(0) instanceof GuiIngame
                && !isAltKeyDown();
    }

    public static void shutdown() {
        assert isRunning();
        INST.running = false;
    }
    public static boolean isRunning() { return INST.running; }

    public static AudioEngine getAudioEngine() { return INST.audioEngine; }
    public static RenderEngine getRenderEngine() { return renderEngine; }

    public static WorldClient getWorld() { return INST.world; }
    public static EntityPlayerSP getPlayer() { return INST.player; }

    public static GuiRoot getRootGUI() { return INST.rootGUI; }
    public static Window getWindow() { return INST.window; }
    public static Camera getCamera() { return INST.camera; }
    public static GameTimer getTimer() { return INST.timer; }
    public static RayPicker getRayPicker() { return INST.rayPicker; }
    public static Scheduler getScheduler() { return INST.scheduler; }
    public static Profiler getProfiler() { return INST.profiler; }

    public static float getDelta() { return INST.timer.getDelta(); }
    public static double getProgramTime() { return Window.getPrecisionTime(); }  // getSystemTime().

    public static float getWidth() { return INST.window.getWidth() / GUI_SCALE; }
    public static float getHeight() { return INST.window.getHeight() / GUI_SCALE; }
    public static float getMouseX() { return INST.window.getMouseX() / GUI_SCALE; }
    public static float getMouseY() { return INST.window.getMouseY() / GUI_SCALE; }

    // Frame-based deltas.
    public static float getMouseDX() { return INST.window.getMouseDX() / GUI_SCALE; }
    public static float getMouseDY() { return INST.window.getMouseDY() / GUI_SCALE; }
    public static float getDScroll() { return INST.window.getDScroll();}

    public static boolean isMouseDown(int button) { return INST.window.isMouseDown(button); }
    public static boolean isKeyDown(int key) { return INST.window.isKeyDown(key); }
    public static boolean isCtrlKeyDown() { return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL); }
    public static boolean isShiftKeyDown() { return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT); }
    public static boolean isAltKeyDown() { return isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT); }
    public static void setMouseGrabbed(boolean grabbed) { INST.window.setMouseGrabbed(grabbed); }
    public static String getClipboard() { return INST.window.getClipboard(); }
    public static void setClipboard(String s) { INST.window.setClipboard(s); }

    // framebuffer_size == window_size * os_content_scale == (guiCoords * GUI_SCALE)* os_content_scale
    public static int toMainFramebufferCoords(float guiCoords) {
        return (int)(guiCoords * GUI_SCALE);
    }
    public static float GUI_FBO_SIZE_FACTOR = 1;
    public static int toGuiFramebufferCoords(float guiCoords) {
        return (int)(guiCoords * GUI_FBO_SIZE_FACTOR);
    }

    public static BitmapImage screenshot(float gx, float gy, float gwidth, float gheight) {
        int wid= toMainFramebufferCoords(gwidth), hei= toMainFramebufferCoords(gheight), x= toMainFramebufferCoords(gx), y= toMainFramebufferCoords(getHeight()-gy-gheight);
        ByteBuffer pixels = BufferUtils.createByteBuffer(wid*hei*4);  // memAlloc(wid * hei * 4);
        glReadBuffer(GL_BACK);
        glReadPixels(x, y, wid,hei, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        return BitmapImage.fromGL(wid, hei, pixels);
    }

    public static void printSystemLog() {

        LOGGER.info("OperationSystem {} {}, rt {} {}, VM {} v{}",
                System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("java.version"), System.getProperty("os.arch"),
                System.getProperty("java.vm.name"), System.getProperty("java.vm.version"));
    }

}
