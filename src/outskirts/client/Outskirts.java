package outskirts.client;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import outskirts.client.audio.AudioEngine;
import outskirts.client.gui.Gui;
import outskirts.client.gui.compoents.GuiHotbar;
import outskirts.client.gui.debug.Gui1DNoiseVisual;
import outskirts.client.gui.debug.GuiDebugV;
import outskirts.client.gui.debug.GuiVert3D;
import outskirts.client.gui.ex.GuiIngame;
import outskirts.client.gui.ex.GuiRoot;
import outskirts.client.gui.screen.*;
import outskirts.client.render.Camera;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.DualContouring;
import outskirts.client.render.isoalgorithm.dc.HermiteData;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.lighting.Light;
import outskirts.client.render.renderer.RenderEngine;
import outskirts.client.render.renderer.debug.test.DebugTestRenderer;
import outskirts.client.render.renderer.debug.visualgeo.DebugVisualGeoRenderer;
import outskirts.entity.EntityStaticMesh;
import outskirts.entity.player.EntityPlayerSP;
import outskirts.entity.player.GameMode;
import outskirts.event.Events;
import outskirts.event.client.WindowResizedEvent;
import outskirts.event.client.input.*;
import outskirts.init.Init;
import outskirts.init.Materials;
import outskirts.init.ex.Models;
import outskirts.init.Textures;
import outskirts.material.Material;
import outskirts.mod.Mods;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.GhostShape;
import outskirts.physics.collision.shapes.convex.*;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.*;
import outskirts.util.concurrent.Scheduler;
import outskirts.util.profiler.Profiler;
import outskirts.util.vector.Vector3f;
import outskirts.world.WorldClient;
import outskirts.world.chunk.ChunkPos;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

import static java.lang.Float.NaN;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import static outskirts.client.ClientSettings.*;
import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;
import static outskirts.util.SystemUtil.IS_OSX;
import static outskirts.util.logging.Log.LOGGER;

public class Outskirts {

    // framebuffer coords.
    private float dScroll, mouseDX, mouseDY, mouseX, mouseY;
    private float width, height; // Framebuffer Coords. not GuiCoords.
    private long window; // glfwWindow
    private float OS_CONTENT_SCALE; // window_size * OS_CONTENT_SCALE = fb_size.

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
        this.initWindowFurther();

        Init.registerAll(Side.CLIENT);

        player = new EntityPlayerSP();
        camera.getCameraUpdater().setOwnerEntity(player);
        player.setName("Player215");

        getRootGUI().addGui(GuiScreenMainMenu.INSTANCE);

        GuiScreenMainMenu.INSTANCE.addGui(new Gui1DNoiseVisual().exec(g -> {
            g.setX(30);
            g.setY(30);
            g.setWidth(800);
            g.setHeight(80);
        }));

//        GuiIngame.INSTANCE.addGui(new GuiHotbar().exec(g -> {
//            g.addLayoutorAlignParentLTRB(30, NaN, NaN, 40);
//        }));

        GuiIngame.INSTANCE.addGui(GuiDebugV.INSTANCE).exec(g -> g.setVisible(false));
        GuiIngame.INSTANCE.addGui(GuiVert3D.INSTANCE).exec(g -> g.setVisible(false));


        debugTestRenderer = new DebugTestRenderer();

        debugVisualGeoRenderer = new DebugVisualGeoRenderer();
    }
    //todo: GameRule LS   Separator/ NonCollision
    //tod0: DebugV OP. options

    DebugTestRenderer debugTestRenderer;
    DebugVisualGeoRenderer debugVisualGeoRenderer;

    private static EntityStaticMesh entityStaticMesh;
    {
        SystemUtil.debugAddKeyHook(GLFW_KEY_I, () -> {
            ChunkPos cp = ChunkPos.of(player.position());
            Vector3f bs = vec3(cp.x, 0, cp.z);
            Octree nd = world.getOctree(bs);

            Octree.forEach(nd, (n,m,s) -> {
                if (n.isLeaf()) {
                    Octree.Leaf lf = (Octree.Leaf)n;
                    if (vec3(lf.min).add(bs).setY(0).sub(vec3(player.position()).setY(0)).length() < 4) {
                        lf.material = isMouseDown(2) ? Materials.DIRT : Materials.GRASS;
                    }
                }
            }, bs, 16);

            world.crd.markRebuild(bs);
        });
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

        profiler.push("glfwPollEvents");
        glfwPollEvents();
        profiler.pop("glfwPollEvents");

        camera.getCameraUpdater().update();
        if (world != null) {
            rayPicker.getRayOrigin().set(getCamera().getPosition());
            rayPicker.getRayDirection().set(getCamera().getCameraUpdater().getDirection());
            rayPicker.update();
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
            glDisable(GL_DEPTH_TEST);
            rootGUI.onLayout();
            rootGUI.onDraw();
            if (world!=null&&rayPicker.getCurrentEntity() != null) {
                Vector3f bp = rayPicker.getCurrentPoint();
                Vector3f base = Vector3f.floor(vec3(bp), 16f);
                Octree nd = world.getOctree(bp);
            }
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

        Light lightSun = new Light();
        lightSun.position().set(40, 50, 40);
        lightSun.color().set(1, 1, 1).scale(1.2f);
        world.lights.add(lightSun);
        SystemUtil.debugAddKeyHook(GLFW_KEY_E, () -> {
            lightSun.position().set(getPlayer().position());
        });

        world.addEntity(entityStaticMesh=new EntityStaticMesh());

        getPlayer().setModel(Models.GEO_SPHERE);
        getPlayer().getRenderPerferences().setDiffuseMap(Textures.BRICK);
        getPlayer().getRenderPerferences().setNormalMap(Textures.BRICK_NORM);
        getPlayer().tmp_boxSphere_scale.set(.4f,0.5f,.4f).scale(1);
        RigidBody prb = getPlayer().getRigidBody();
//        prb.setCollisionShape(new BoxShape(.2f,1f,.2f));
//        prb.setCollisionShape(new SphereShape(.5f));
        prb.setCollisionShape(new CapsuleShape(.4f, .6f));  // .4f,0.5f,.4f
//        prb.setCollisionShape(new ConvexHullShape(QuickHull.quickHull(BP.attribute(0).data)));
        prb.setCollisionShape(new GhostShape());
        prb.transform().set(Transform.IDENTITY);
        prb.transform().origin.set(0,52,0);
        prb.getAngularVelocity().scale(0);
        prb.getLinearVelocity().scale(0);
        prb.setMass(10);
        prb.setFriction(0.2f);
        prb.setRestitution(0f);
         prb.setInertiaTensorLocal(0,0,0);

         getPlayer().setGamemode(GameMode.CREATIVE);

    }

    private void runTick() {

        if (getWorld() != null) {
            if (isIngame()) {
                // MOVEMENT
                float lv =0.5f;
                if (Outskirts.isKeyDown(GLFW_KEY_F))
                    lv *= 6;
                if (KEY_WALK_FORWARD.isKeyDown()) player.walk(lv, 0);
                if (KEY_WALK_BACKWARD.isKeyDown()) player.walk(lv, Maths.PI);
                if (KEY_WALK_LEFT.isKeyDown()) player.walk(lv, Maths.PI/2);
                if (KEY_WALK_RIGHT.isKeyDown()) player.walk(lv, -Maths.PI/2);
                if (KEY_JUMP.isKeyDown()  &&
                        (player.getGamemode() == GameMode.CREATIVE ||
                        (player.getGamemode() == GameMode.SURVIVAL && player.isOnGround()))) player.walk(2.4f, new Vector3f(0, 1, 0));
                if (KEY_SNEAK.isKeyDown() && player.getGamemode() == GameMode.CREATIVE) player.walk(lv, new Vector3f(0, -1, 0));

//                // ITEM USE
//                float CONTINUE_USE_INTERVAL = 0.16f;
//                long t = Outskirts.getSystemTime();
//                ItemStack holdingItem = player.getHotbarItem();
//                if (KEY_USE.isKeyDown() && lastItemUseTime+(long)(CONTINUE_USE_INTERVAL*1000) < t && !holdingItem.empty()) {
//                    holdingItem.getItem().onItemUse(getWorld(), holdingItem);
//                    lastItemUseTime=t;
//                }


//                if (isMouseDown(0) || isMouseDown(1)) {
//                    Vector3f blockpos = rayPicker.getCurrentBlockPos();
//                    Block b = world.getBlock(blockpos);
//                    b.v += isMouseDown(0) ? -0.01f : 0.01f;
//                    b.v = Maths.clamp(b.v, -1.0f, 1.0f);
//                    world.setBlock(blockpos, b);
//                }

//                if (isKeyDown(GLFW_KEY_I))
//                    Outskirts.getWorld().setBlock(rayPicker.getCurrentBlockPos(), new BlockStone());



                ClientSettings.FOV=Outskirts.isKeyDown(GLFW_KEY_C)?30:80;
            }


            profiler.push("world");
            world.onTick();
            profiler.pop("world");
        }
    }

    private void destroy() {

        getRootGUI().removeAllGuis();  // dispatches Gui.DetachEvent.
        ClientSettings.saveOptions();

        Loader.destroy();
        audioEngine.destroy();

        GL.setCapabilities(null);
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    // for: Camera mouse-view, walk-keyboard. ingame pause. key-use/attack.
    public static boolean isIngame() {
        return Outskirts.getWorld() != null && Outskirts.getRootGUI().size() == 1 && Outskirts.getRootGUI().getGui(0) instanceof GuiIngame;
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

    public static RayPicker getRayPicker() {
        return INSTANCE.rayPicker;
    }

    public static WorldClient getWorld() {
        return INSTANCE.world;
    }

    public static float getDelta() {
        return INSTANCE.timer.getDelta();
    }

    public static Camera getCamera() {
        return INSTANCE.camera;
    }

    public static long getSystemTime() {
        return System.currentTimeMillis();
    }


    // the DX/DY is high level in actually operation, in MacOSX DX'll automaclly be attachs by holding Shift key. but in windows not.
    public static float getDScroll() {
        return INSTANCE.dScroll / INSTANCE.OS_CONTENT_SCALE;
    }

    public static float getMouseDX() {
        return INSTANCE.mouseDX / GUI_SCALE / INSTANCE.OS_CONTENT_SCALE;
    }
    public static float getMouseDY() {
        return INSTANCE.mouseDY / GUI_SCALE / INSTANCE.OS_CONTENT_SCALE;
    }

    public static float getMouseX() {
        return INSTANCE.mouseX / GUI_SCALE / INSTANCE.OS_CONTENT_SCALE;
    }
    public static float getMouseY() {
        return INSTANCE.mouseY / GUI_SCALE / INSTANCE.OS_CONTENT_SCALE;
    }

    public static float getWidth() {
        return INSTANCE.width / GUI_SCALE / INSTANCE.OS_CONTENT_SCALE;
    }
    public static float getHeight() {
        return INSTANCE.height / GUI_SCALE / INSTANCE.OS_CONTENT_SCALE;
    }

    public static boolean isCtrlKeyDown() {
        if (IS_OSX)
            return glfwGetKey(INSTANCE.window, GLFW_KEY_LEFT_SUPER) == GLFW_PRESS || glfwGetKey(INSTANCE.window, GLFW_KEY_RIGHT_SUPER) == GLFW_PRESS;
        else
            return glfwGetKey(INSTANCE.window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS || glfwGetKey(INSTANCE.window, GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS;
    }
    public static boolean isShiftKeyDown() {
        return glfwGetKey(INSTANCE.window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS || glfwGetKey(INSTANCE.window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS;
    }
    public static boolean isAltKeyDown() {
        return glfwGetKey(INSTANCE.window, GLFW_KEY_LEFT_ALT) == GLFW_PRESS || glfwGetKey(INSTANCE.window, GLFW_KEY_RIGHT_ALT) == GLFW_PRESS;
    }

    public static boolean isMouseDown(int button) {
        return glfwGetMouseButton(INSTANCE.window, button) == GLFW_PRESS;
    }
    public static boolean isKeyDown(int key) {
        return glfwGetKey(INSTANCE.window, key) == GLFW_PRESS;
    }

    public static void setMouseGrabbed(boolean grabbed) {
        glfwSetInputMode(INSTANCE.window, GLFW_CURSOR, grabbed?GLFW_CURSOR_DISABLED: GLFW_CURSOR_NORMAL);
    }

    // there is not requires AWT. useful in OSX
    public static String getClipboard() {
        return glfwGetClipboardString(INSTANCE.window);
    }
    public static void setClipboard(String s) {
        glfwSetClipboardString(INSTANCE.window, s);
    }

    // framebuffer_size = window_size * os_content_scale = (guiCoords * GUI_SCALE)==window_size * os_content_scale
    public static int toFramebufferCoords(float guiCoords) {
        return (int)((guiCoords * GUI_SCALE) * INSTANCE.OS_CONTENT_SCALE);
    }

    // params: GUI coords.
    private static BufferedImage screenshot(float gx, float gy, float gwidth, float gheight) {
        int wid=toFramebufferCoords(gwidth), hei=toFramebufferCoords(gheight), x=toFramebufferCoords(gx), y=toFramebufferCoords(getHeight()-gy-gheight);
        ByteBuffer pixels = memAlloc(wid * hei * 4);
        try {
            glReadBuffer(GL_BACK);
            glReadPixels(x, y, wid,hei, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            return Loader.loadImage(pixels, wid, hei);
        } finally {
            memFree(pixels);
        }
    }

    private void createDisplay() throws IOException {

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW.");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); // MAC OSX requires

        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);

        window = glfwCreateWindow(1, 1, "ENGNNE", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create window.");

//        glfwSetWindowIcon(window, new GLFWImage.Buffer(
//                Loader.loadTextureData(Loader.loadPNG(new Identifier("textures/gui/icons/icon_32x32.png").getInputStream()), false)
//        ));

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        GL.createCapabilities();

        glfwSetFramebufferSizeCallback(window, this::onWindowResized);
        glfwSetCursorPosCallback(window, this::onMouseMove);
        glfwSetScrollCallback(window, this::onScroll);
        glfwSetMouseButtonCallback(window, this::onMouseButton);
        glfwSetKeyCallback(window, this::onKeyboard);
        glfwSetCharCallback(window, this::onCharInput);

        LOGGER.info("OperationSystem {} {}, rt {} {}, VM {} v{}",
                System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("java.version"), System.getProperty("os.arch"),
                System.getProperty("java.vm.name"), System.getProperty("java.vm.version"));
    }

    private void updateDisplay() {

        if (glfwWindowShouldClose(window)) {
            Outskirts.shutdown();
        }

        // needs been update in sometimes more. not just when startup init. e.g. when the window switched to another display monitor.
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer scaleX = stack.callocFloat(1), scaleY = stack.callocFloat(1);
            glfwGetWindowContentScale(INSTANCE.window, scaleX, scaleY);
            Validate.isTrue(scaleX.get(0) == scaleY.get(0), "Unsupported scale type: x != y");
            OS_CONTENT_SCALE = scaleX.get(0);
//            LOGGER.info("OSC: "+OS_CONTENT_SCALE);
        }

        // clear curr delta in frame tail
        dScroll = 0;
        mouseDX = 0;
        mouseDY = 0;

        glfwSwapBuffers(window);

        // SystemUtils.nanosleep();
        // Display.sync(GameSettings.FPS_CAPACITY);
    }

    private void initWindowFurther() {
        this.updateDisplay(); // update CONTENT_SCALE for correctly init-Viewport (set tha before init-onResize()

        // set window to screen center and perform init-onResize()
        GLFWVidMode wVidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        int wWidth = ClientSettings.ProgramArguments.WIDTH, wHeight = ClientSettings.ProgramArguments.HEIGHT;
        glfwSetWindowPos(window, wVidmode.width() / 2 - wWidth / 2, wVidmode.height() / 2 - wHeight / 2); // make window center
        glfwSetWindowSize(window, wWidth, wHeight); // invokes onResize(). for init on macOS
        glfwShowWindow(window);
    }

    private float glfwRawToFbCoord(float raw) {
        return IS_OSX ? raw*OS_CONTENT_SCALE : raw;
    }

    /**
     * Window Creation, Window Resized, Window toggleFullscreen
     */
    private void onWindowResized(long w, int nWidth, int nHeight) {
        this.width = nWidth;
        this.height = nHeight;

        Events.EVENT_BUS.post(new WindowResizedEvent());

        glViewport(0, 0, toFramebufferCoords(getWidth()), toFramebufferCoords(getHeight()));
    }

    private void onMouseMove(long w, double nX, double nY) {
        float oldmx = mouseX, oldmy = mouseY;
        {
            mouseX = glfwRawToFbCoord((float)nX);
            mouseY = glfwRawToFbCoord((float)nY);
        }
        mouseDX = mouseX - oldmx;
        mouseDY = mouseY - oldmy;
        Events.EVENT_BUS.post(new MouseMoveEvent());
    }

    private void onScroll(long w, double dX, double dY) {
        dScroll = glfwRawToFbCoord((float)(dX + dY));
        Events.EVENT_BUS.post(new MouseScrollEvent());

    }

    private void onMouseButton(long w, int button, int action, int mods) {
        Events.EVENT_BUS.post(new MouseButtonEvent(button, action));

        KeyBinding.postInput(button, action==GLFW_PRESS, KeyBinding.TYPE_MOUSE);
    }

    private void onKeyboard(long w, int key, int scancode, int action, int mods) {
        if (action == GLFW_REPEAT)
            return;
        Events.EVENT_BUS.post(new KeyboardEvent(key, action));

        KeyBinding.postInput(key, action==GLFW_PRESS, KeyBinding.TYPE_KEYBOARD);
    }

    private void onCharInput(long w, int ch) {
        Events.EVENT_BUS.post(new CharInputEvent(ch));
    }
}
