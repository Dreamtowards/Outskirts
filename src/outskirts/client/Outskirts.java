package outskirts.client;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import outskirts.client.audio.AudioEngine;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiPopupMenu;
import outskirts.client.gui.debug.*;
import outskirts.client.gui.ex.GuiRoot;
import outskirts.client.gui.ex.GuiTestWindowWidgets;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.screen.*;
import outskirts.client.material.Texture;
import outskirts.client.render.Camera;
import outskirts.client.render.renderer.RenderEngine;
import outskirts.entity.player.EntityPlayerSP;
import outskirts.event.Event;
import outskirts.event.Events;
import outskirts.event.client.WindowResizedEvent;
import outskirts.event.client.input.*;
import outskirts.init.Init;
import outskirts.init.Models;
import outskirts.init.SceneIniter;
import outskirts.init.Textures;
import outskirts.mod.Mods;
import outskirts.physics.collision.shapes.convex.*;
import outskirts.storage.dat.DSTUtils;
import outskirts.storage.DataMap;
import outskirts.util.*;
import outskirts.util.concurrent.Scheduler;
import outskirts.util.profiler.Profiler;
import outskirts.util.vector.Vector3f;
import outskirts.world.WorldClient;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import static outskirts.client.ClientSettings.GUI_SCALE;
import static outskirts.util.logging.Log.LOGGER;

public class Outskirts {

    // dScroll: wheelDXY. all windowCoords
    private float dScroll, mouseDX, mouseDY, mouseX, mouseY; // WindowCoords
    private float width, height; // WindowCoords. not FramebufferCoords, not GuiCoords.
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

        {   // set window to screen center and perform init-onResize()
            GLFWVidMode wVidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
            int wWidth = ClientSettings.ProgramArguments.WIDTH, wHeight = ClientSettings.ProgramArguments.HEIGHT;
            glfwSetWindowPos(window, wVidmode.width() / 2 - wWidth / 2, wVidmode.height() / 2 - wHeight / 2); // make window center
            glfwSetWindowSize(window, wWidth, wHeight); // for init onResize() in macOS
            glfwShowWindow(window);

            this.updateDisplay(); // update CONTENT_SCALE for correct init viewport (before init-round onResize()/glfwPollEvents()
        }

        Init.registerAll(Side.CLIENT);

        player = new EntityPlayerSP();
        camera.getCameraUpdater().setOwnerEntity(player);

        getRootGUI().addGui(GuiDebugCommon.INSTANCE); GuiDebugCommon.INSTANCE.setVisible(false);
        startScreen(GuiScreenMainMenu.INSTANCE);

        getRootGUI().addGui(new GuiWindow(new GuiTestWindowWidgets()));
        tmpTex = Loader.loadTexture(new FileInputStream("/Users/dreamtowards/Downloads/tmptex.jpeg"));

        Gui gParent = getRootGUI().addGui(new Gui());
        gParent.addOnDrawListener(e -> Gui.drawRect(Colors.RED, gParent));
        gParent.setWidth(100);
        gParent.setHeight(100);
        gParent.setX(100);
        gParent.setY(100);
        gParent.addLayoutorWrapChildren(1,1,1,1);

        Gui gChild = gParent.addGui(new Gui());
        gChild.addOnDrawListener(e -> Gui.drawRect(Colors.GREEN, gChild));
        gChild.setWidth(50);
        gChild.setHeight(50);
        gChild.addLayoutorAlignParentLTRB(1, Float.NaN, 1, Float.NaN);

        GuiPopupMenu menu = getRootGUI().addGui(new GuiPopupMenu());
        menu.addItem(GuiPopupMenu.Item.button("Op1"));
        menu.addItem(GuiPopupMenu.Item.button("Op2"));
        menu.addItem(GuiPopupMenu.Item.button("Op3"));
        menu.addItem(GuiPopupMenu.Item.button("Op4"));
        menu.addItem(GuiPopupMenu.Item.bswitch("OpSel1", true, b -> {}));
        menu.addItem(GuiPopupMenu.Item.bswitch("OpSel2", true, b -> {}));
        menu.addItem(GuiPopupMenu.Item.bswitch("OpSel3", true, b -> {}));

        Events.EVENT_BUS.register(MouseButtonEvent.class, e -> {

            if (e.getButtonState() && e.getMouseButton() == GLFW_MOUSE_BUTTON_RIGHT) {


                LOGGER.info("Show");

                menu.show(getMouseX(), getMouseY());

            }
        });

        Events.EVENT_BUS.register(Event.class, e -> {

            LOGGER.info("Event: {}", e.getClass());
        });
    }
    private Texture tmpTex;

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

        if (world != null)
            rayPicker.update();

        // Render Phase
        profiler.push("render");
        {
            renderEngine.prepare();
//            SystemUtils.sleep(1000);

            profiler.push("world");
            if (world != null) {
                renderEngine.render(world);
            }
            profiler.pop("world");

            profiler.push("gui");
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);  // gui face flip render requires. (negatives width/height)

            Gui.drawTexture(Outskirts.renderEngine.getWorldFramebuffer().colorTextures(0), getRootGUI());
            rootGUI.onLayout();
            rootGUI.onDraw();

//            Gui.drawCornerStretchTexture(tmpTex, 100, 100, 100, 100, 45);

            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);

            profiler.pop("gui");
        }
        profiler.pop("render");

        profiler.pop("rt");
        profiler.push("updateDisplay");
        this.updateDisplay();
        profiler.pop("updateDisplay");
    }

    public static void setWorld(WorldClient world) {
        INSTANCE.world = world;
        if (world == null)
            return;
//        try {
//            world.onRead(DSTUtils.read(new FileInputStream("scen.dat")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        SceneIniter.init(world);

//        ModelData[] mdat = new ModelData[1];
//        EntityGeoShape eFloor = new EntityGeoShape(new BoxShape(100,10,100));
//        INSTANCE.world.addEntity(eFloor);
//        eFloor.tmp_boxSphere_scale.set(1,1,1);
//        eFloor.getRigidBody().getGravity().set(0,0,0);
////        eFloor.getRigidBody().transform().origin.set(0, -10, 0);
//        eFloor.getRigidBody().setMass(8000*0f).setRestitution(0.1f).transform().origin.y=5;
//        eFloor.getMaterial()
//                .setModel(Loader.loadOBJ(new Identifier("materials/mount/part.obj").getInputStream(), mdat))
//                .setDiffuseMap(Textures.WOOD1)
//                .setSpecularStrength(0.1f).setShininess(20);
//        eFloor.getRigidBody().setCollisionShape(new TriangleMeshShape(mdat[0].indices, mdat[0].positions));


        getPlayer().setModel(Models.GEO_CUBE);
        getPlayer().getMaterial().setDiffuseMap(Textures.CONTAINER);
        getPlayer().getRigidBody().setCollisionShape(new BoxShape(.5f,.5f,.5f));
//        getPlayer().getRigidBody().setCollisionShape(new SphereShape(.5f));
//        getPlayer().getRigidBody().setCollisionShape(new CapsuleShape(.5f, .5f));
        getPlayer().tmp_boxSphere_scale.set(1,1,1).scale(0.5f);
        getPlayer().getRigidBody().transform().set(Transform.IDENTITY);
        getPlayer().getRigidBody().transform().origin.set(0,20,20);
        getPlayer().getRigidBody().getGravity().set(0, -20, 0).scale(1);
        getPlayer().getRigidBody().getAngularVelocity().scale(0);
        getPlayer().getRigidBody().getLinearVelocity().scale(0);
        getPlayer().getRigidBody().setMass(20);//.setFriction(0.5f).setRestitution(0f);//.setLinearDamping(0.04f);
//        getPlayer().getRigidBody().invInertiaTensorLocalDiag = SceneIniter.e.getRigidBody().invInertiaTensorLocalDiag;
//        getPlayer().getRigidBody().setInertiaTensorLocal(0,0,0);


        Events.EVENT_BUS.register(KeyboardEvent.class, e -> {
            if (e.getKeyState()) {
                if (e.getKey() == GLFW_KEY_C)
                    GuiVert3D.INSTANCE.vertices.clear();
                if (e.getKey() == GLFW_KEY_T) {
//                    INSTANCE.animatedModel.animator.doAnimation(INSTANCE.animation);
//                    Matrix4f.translate(new Vector3f(20, 0, 10), INSTANCE.animatedModel.joints[6].currentTransform);
//                    Outskirts.getWorld().lights.get(0).getPosition().set(getCamera().getPosition());
//                    getPlayer().getRigidBody().getAngularVelocity().add(100, 0, 0);
//                    for (CollisionObject r : Outskirts.getWorld().dynamicsWorld.getCollisionObjects()) {
//                        ((RigidBody)r).setInertiaTensorLocal(0,0,0);
//                    }
                }
                if (e.getKey() == GLFW_KEY_1) {
//                    getCamera().getPosition().set(0, 0, 10);
//                    GuiVert3D.INSTANCE.vertices.clear();
//                    for (int i = 0;i < jts.length;i++) {
//                        Vector4f vo = Matrix4f.transform(jts[i]._bindTransform, new Vector4f(0,0,0,1));
//                        Vector4f vc = Matrix4f.transform(jts[i].currentTransform, new Vector4f(0,0,0,1));
//                        GuiVert3D.addVert("jo-"+jts[i].name, new Vector3f(vo.x, vo.y, vo.z), Colors.GREEN, i==0?new String[0]:new String[]{"jo-"+jts[jts[i].parentIdx].name});
//                        GuiVert3D.addVert("jc-"+jts[i].name, new Vector3f(vc.x, vc.y, vc.z), Colors.RED, i==0?new String[0]:new String[]{"jc-"+jts[jts[i].parentIdx].name});
//                    }
                }
            }
        });
    }
    public static void setPauseWorld(float speed) {
        if (getWorld()==null)return;
//        if (speed == 0) speed = 0.000001f;
        getCamera().getCameraUpdater().setOwnerEntity(speed==1.0f ? getPlayer() : null);
        getWorld().tmpTickFactor = speed;
    }

    private void runTick() {

        if (getWorld() != null) {
            if (currentScreen() == null) {
                float lv =1;
                if (Outskirts.isKeyDown(GLFW_KEY_F))
                    lv *= 6;
                if (ClientSettings.KEY_WALK_FORWARD.isKeyDown()) player.walkStep(lv, 0);
                if (ClientSettings.KEY_WALK_BACKWARD.isKeyDown()) player.walkStep(lv, Maths.PI);
                if (ClientSettings.KEY_WALK_LEFT.isKeyDown()) player.walkStep(lv, Maths.PI/2);
                if (ClientSettings.KEY_WALK_RIGHT.isKeyDown()) player.walkStep(lv, -Maths.PI/2);
                if (ClientSettings.KEY_JUMP.isKeyDown()) player.walkStep(lv, new Vector3f(0, 1, 0));
                if (ClientSettings.KEY_SNEAK.isKeyDown()) player.walkStep(lv, new Vector3f(0, -1, 0));

                ClientSettings.FOV=Outskirts.isKeyDown(GLFW_KEY_C)?30:80;
            }


            profiler.push("world");
            world.onTick();
            profiler.pop();
        }
    }

    private void destroy() {

        if (getWorld() != null) {
            try {
                DSTUtils.write(getWorld().onWrite(new DataMap()), new FileOutputStream("scen.dat"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        ClientSettings.saveOptions();

        Loader.destroy();

        audioEngine.destroy();

        GL.setCapabilities(null);
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public static GuiScreen currentScreen() {
        return getRootGUI().currentScreen();
    }
    public static void startScreen(GuiScreen screen) {
        getRootGUI().addGui(screen);
    }
    public static GuiScreen closeScreen() {
        return getRootGUI().closeScreen();
    }
    public static void closeAllScreen() {
        getRootGUI().closeAllScreen();
    }

    public static boolean isRunning() {
        return INSTANCE.running;
    }

    public static void shutdown() {
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


    // the DX/DY is high level in actually operation, in MacOSX DX'll automaclly be attachs by holding Shift key. but in windows not.
    public static float getDScroll() {
        return INSTANCE.dScroll;
    }

    public static float getMouseDX() {
        return INSTANCE.mouseDX / GUI_SCALE;
    }
    public static float getMouseDY() {
        return INSTANCE.mouseDY / GUI_SCALE;
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
        if (SystemUtils.IS_OS_MAC)
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
    private static BufferedImage makeScreenshot(float gx, float gy, float gwidth, float gheight) {
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

        window = glfwCreateWindow(1, 1, "ENGINE", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create window.");

//        glfwSetWindowIcon(window, new GLFWImage.Buffer(
//                Loader.loadTextureData(Loader.loadPNG(new Identifier("textures/gui/icons/icon_32x32.png").getInputStream()), false)
//        ));

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        GL.createCapabilities();

        glfwSetWindowSizeCallback(window, this::onWindowResized);
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

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer scaleX = stack.callocFloat(1);
            FloatBuffer scaleY = stack.callocFloat(1);
            glfwGetWindowContentScale(INSTANCE.window, scaleX, scaleY);
            Validate.isTrue(scaleX.get(0) == scaleY.get(0), "Unsupported scale type: x != y");
            OS_CONTENT_SCALE = scaleX.get(0);
        }

        // clear curr delta in frame tail
        dScroll = 0;
        mouseDX = 0;
        mouseDY = 0;

        glfwSwapBuffers(window);

        // SystemUtils.nanosleep();
        // Display.sync(GameSettings.FPS_CAPACITY);
    }


    /**
     * Window Creation, Window Resized, Window toggleFullscreen
     */
    private void onWindowResized(long w, int nWidth, int nHeight) {
        Events.EVENT_BUS.post(new WindowResizedEvent());

        this.width = nWidth;
        this.height = nHeight;

        glViewport(0, 0, toFramebufferCoords(getWidth()), toFramebufferCoords(getHeight()));
    }

    private void onMouseMove(long w, double nX, double nY) {
        float oldmx = mouseX, oldmy = mouseY;
        mouseX =  (float) nX;
        mouseY =  (float) nY;
        mouseDX = mouseX - oldmx;
        mouseDY = mouseY - oldmy;
        Events.EVENT_BUS.post(new MouseMoveEvent());
        rootGUI.broadcaseEvent(new MouseMoveEvent());
    }

    private void onScroll(long w, double dX, double dY) {
        dScroll = (float)(dX + dY);
        Events.EVENT_BUS.post(new MouseScrollEvent());
        rootGUI.broadcaseEvent(new MouseScrollEvent());

    }

    private void onMouseButton(long w, int button, int action, int mods) {
        Events.EVENT_BUS.post(new MouseButtonEvent(button, action));
        rootGUI.broadcaseEvent(new MouseButtonEvent(button, action));

        KeyBinding.postInput(button, action==GLFW_PRESS, KeyBinding.TYPE_MOUSE);
    }

    private void onKeyboard(long w, int key, int scancode, int action, int mods) {
        if (action == GLFW_REPEAT)
            return;
        Events.EVENT_BUS.post(new KeyboardEvent(key, action));
        rootGUI.broadcaseEvent(new KeyboardEvent(key, action));

        KeyBinding.postInput(key, action==GLFW_PRESS, KeyBinding.TYPE_KEYBOARD);
    }

    private void onCharInput(long w, int ch) {
        Events.EVENT_BUS.post(new CharInputEvent(ch));
        rootGUI.broadcaseEvent(new CharInputEvent(ch));
    }
}
